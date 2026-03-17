package com.project.evaluation.vo.RuleCategory;

import lombok.Data;

/**
 * 更新规则分类请求体
 */
@Data
public class UpdateRuleCategoryReq {

    /**
     * 规则总览 id
     */
    private Integer ruleId;

    /**
     * 分类名
     */
    private String categoryName;

    /**
     * 分类父级 id
     */
    private Integer parentId;
}
