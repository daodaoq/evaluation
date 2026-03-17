package com.project.evaluation.vo.RuleItem;

import lombok.Data;

/**
 * 删除规则项请求体
 */
@Data
public class DeleteRuleItemReq {
    /**
     * 待删除的 id
     */
    private Integer id;
}
