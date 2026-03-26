package com.project.evaluation.utils;

import com.project.evaluation.config.MinioProperties;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class MinioUtil {
    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProperties minioProperties;

    @PostConstruct
    public void init() {
        createDefaultBucketIfAbsent();
    }

    public String getDefaultBucket() {
        return minioProperties.getBucketName();
    }

    public boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException("检查桶是否存在失败: " + bucketName, e);
        }
    }

    public void createBucketIfAbsent(String bucketName) {
        try {
            if (!bucketExists(bucketName)) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("创建桶失败: " + bucketName, e);
        }
    }

    public void createDefaultBucketIfAbsent() {
        createBucketIfAbsent(getDefaultBucket());
    }

    public String upload(MultipartFile file, String objectName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        String finalObjectName = StringUtils.hasText(objectName) ? objectName : file.getOriginalFilename();
        if (!StringUtils.hasText(finalObjectName)) {
            throw new IllegalArgumentException("objectName 不能为空");
        }
        try (InputStream is = file.getInputStream()) {
            createDefaultBucketIfAbsent();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(getDefaultBucket())
                            .object(finalObjectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return finalObjectName;
        } catch (Exception e) {
            throw new RuntimeException("上传文件失败: " + finalObjectName, e);
        }
    }

    public String upload(InputStream inputStream, long size, String objectName, String contentType) {
        if (inputStream == null) throw new IllegalArgumentException("inputStream 不能为空");
        if (!StringUtils.hasText(objectName)) throw new IllegalArgumentException("objectName 不能为空");
        try {
            createDefaultBucketIfAbsent();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(getDefaultBucket())
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );
            return objectName;
        } catch (Exception e) {
            throw new RuntimeException("上传流失败: " + objectName, e);
        }
    }

    public InputStream getObject(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(getDefaultBucket())
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("下载对象失败: " + objectName, e);
        }
    }

    public boolean objectExists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(getDefaultBucket())
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse() != null && "NoSuchKey".equalsIgnoreCase(e.errorResponse().code())) {
                return false;
            }
            throw new RuntimeException("检查对象失败: " + objectName, e);
        } catch (Exception e) {
            throw new RuntimeException("检查对象失败: " + objectName, e);
        }
    }

    public String getPreviewUrl(String objectName, int expiry, TimeUnit unit) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(getDefaultBucket())
                            .object(objectName)
                            .expiry(expiry, unit)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("生成预览地址失败: " + objectName, e);
        }
    }

    public void removeObject(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(getDefaultBucket())
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("删除对象失败: " + objectName, e);
        }
    }

    public List<String> removeObjects(List<String> objectNames) {
        if (objectNames == null || objectNames.isEmpty()) return List.of();
        List<DeleteObject> objects = objectNames.stream().map(DeleteObject::new).toList();
        List<String> failed = new ArrayList<>();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                RemoveObjectsArgs.builder()
                        .bucket(getDefaultBucket())
                        .objects(objects)
                        .build()
        );
        for (Result<DeleteError> result : results) {
            try {
                DeleteError error = result.get();
                failed.add(error.objectName());
            } catch (Exception e) {
                failed.add("unknown");
            }
        }
        return failed;
    }

    public List<String> listObjects(String prefix, boolean recursive) {
        List<String> list = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(getDefaultBucket())
                        .prefix(prefix)
                        .recursive(recursive)
                        .build()
        );
        for (Result<Item> result : results) {
            try {
                list.add(result.get().objectName());
            } catch (Exception e) {
                throw new RuntimeException("列举对象失败", e);
            }
        }
        return list;
    }
}

