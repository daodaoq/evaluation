package com.project.evaluation.vo.RuleCategory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 删除规则分类请求体
 */
@Data
public class DeleteRuleCategoryReq {
    /**
     * 待删除的 id
     */
    @NotNull(message = "规则分类ID不能为空")
    @Positive(message = "规则分类ID必须为正数")
    private Integer id;
}
