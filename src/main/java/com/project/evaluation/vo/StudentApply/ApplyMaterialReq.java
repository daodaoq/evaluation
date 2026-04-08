package com.project.evaluation.vo.StudentApply;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplyMaterialReq {
    @Size(max = 255, message = "材料文件名长度不能超过255")
    private String fileName;

    @Size(max = 500, message = "材料地址长度不能超过500")
    private String fileUrl;
}
