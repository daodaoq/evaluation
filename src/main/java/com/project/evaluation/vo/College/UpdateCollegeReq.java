package com.project.evaluation.vo.College;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新学院请求体
 */
@Data
public class UpdateCollegeReq {
    /**
     * 学院名称
     */
    @NotBlank(message = "学院名称不能为空")
    @Size(max = 100, message = "学院名称长度不能超过100")
    private String collegeName;

    /**
     * 状态（1启用0停用）
     */
    @NotNull(message = "状态不能为空")
    private  Integer status;
}
