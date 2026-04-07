package com.project.evaluation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    // 启动时从配置文件里自动绑定到这几个字段上
    // @ConfigurationProperties(prefix = "minio")
    // 用前缀 minio 去 application.yml 里找配置，并按名字映射到当前类的属性上。
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName = "evaluation";
}

