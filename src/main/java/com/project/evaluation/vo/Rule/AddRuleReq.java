package com.project.evaluation.vo.Rule;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * 添加规则总览请求体
 */
@Data
public class AddRuleReq {

    /**
     * 综测周期 id
     */
    @NotNull(message = "周期ID不能为空")
    @Positive(message = "周期ID必须为正数")
    private Integer periodId;

    /**
     * 规则总览名称
     */
    @NotBlank(message = "规则名称不能为空")
    @Size(max = 64, message = "规则名称长度不能超过64")
    private String ruleName;

    /**
     * 版本号
     */
    @NotBlank(message = "版本号不能为空")
    @Size(max = 32, message = "版本号长度不能超过32")
    private String versionCode;

    /**
     * 是否启用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
