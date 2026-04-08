package com.project.evaluation.vo.Rule;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 删除规则总览请求体
 */
@Data
public class DeleteRuleReq {
    /**
     * 待删除的 id
     */
    @NotNull(message = "规则ID不能为空")
    @Positive(message = "规则ID必须为正数")
    private Integer id;
}
