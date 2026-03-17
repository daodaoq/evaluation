package com.project.evaluation.vo.RuleCategory;

import lombok.Data;

/**
 * 删除规则分类请求体
 */
@Data
public class DeleteRuleCategoryReq {
    /**
     * 待删除的 id
     */
    private Integer id;
}
