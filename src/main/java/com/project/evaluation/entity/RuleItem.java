package com.project.evaluation.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 综测规则细则项，与表 evaluation_rule_item 对应。
 */
@Data
public class RuleItem {

    /**
     * 规则项 id
     */
    private Long id;

    /**
     * 规则总览 id
     */
    private Long ruleId;

    /**
     * 指标项名称
     */
    private String itemName;

    /**
     * 指标项类型
     * 0-加分项
     * 1-减分项
     */
    private Integer itemType;

    /**
     * 规则项分类
     */
    private Integer itemCategory;

    /**
     * 规则项级别（如：国家级 / 省级 / 校级 等）
     */
    private String level;

    /**
     * 基础分值
     */
    private Double baseScore;

    /**
     * 是否竞赛项
     * 0-非竞赛
     * 1-竞赛项
     */
    private Integer isCompetition;

    /**
     * 是否需要材料
     * 0-不需要
     * 1-需要
     */
    private Integer needMaterial;

    /**
     * 是否启用
     */
    private Integer status;

    /**
     * 计分方式：ADD / SUB / MAX_ONLY
     */
    private String scoreMode;

    /**
     * 同类去重组（同组取最高）
     */
    private String dedupeGroup;

    /**
     * 系数（如第二职务 ×0.5）
     */
    private BigDecimal coeff;

    /**
     * 模块编码：MORAL / ACADEMIC / QUALITY
     */
    private String moduleCode;

    /**
     * 子模块编码
     */
    private String submoduleCode;

    /** 记录创建时间 */
    private LocalDateTime createTime;
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;
}
