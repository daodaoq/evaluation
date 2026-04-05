package com.project.evaluation.vo.RuleCategory;

import lombok.Data;

import java.math.BigDecimal;

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

    private BigDecimal scoreCap;

    private Integer studentVisible;

    private Integer sortOrder;

    private BigDecimal categoryBaseScore;
}
