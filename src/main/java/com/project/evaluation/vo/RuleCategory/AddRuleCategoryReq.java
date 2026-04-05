package com.project.evaluation.vo.RuleCategory;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 添加规则分类请求体
 */
@Data
public class AddRuleCategoryReq {

    /**
     * 插入后回填（复制分类树时使用）
     */
    private Integer id;

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

    /** 默认 1 */
    private Integer studentVisible;

    private Integer sortOrder;

    /** 分类基础分，默认 0 */
    private BigDecimal categoryBaseScore;
}
