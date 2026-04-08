package com.project.evaluation.vo.RuleItem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 删除规则项请求体
 */
@Data
public class DeleteRuleItemReq {
    /**
     * 待删除的 id
     */
    @NotNull(message = "规则项ID不能为空")
    @Positive(message = "规则项ID必须为正数")
    private Integer id;
}
