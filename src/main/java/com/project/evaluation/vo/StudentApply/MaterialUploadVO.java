package com.project.evaluation.vo.StudentApply;

import lombok.Data;

/**
 * 学生端上传申报材料后的返回：fileUrl 为 MinIO 对象键（非浏览器可直接打开的公网 URL）。
 */
@Data
public class MaterialUploadVO {
    private String fileName;
    private String fileUrl;
    private String contentType;
}
