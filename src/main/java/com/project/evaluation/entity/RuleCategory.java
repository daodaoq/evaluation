package com.project.evaluation.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 规则分类（树形），与表 evaluation_rule_item_category 对应。
 */
@Data
public class RuleCategory {

    /**
     * 规则分类 id
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

    /** 该分类子树内加分合计上限；null 表示不限制 */
    private BigDecimal scoreCap;

    /** 1=学生端展示；0=隐藏（子树一并隐藏） */
    private Integer studentVisible;

    /** 同级排序，越小越靠前 */
    private Integer sortOrder;

    /** 该分类节点的基础分（计入本节点小计，并与子分类汇总） */
    private BigDecimal categoryBaseScore;

    /** 记录创建时间 */
    private LocalDateTime createTime;
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;
}
