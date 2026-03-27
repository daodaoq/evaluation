package com.project.evaluation.vo.RuleItem;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新规则项请求体
 */
@Data
public class UpdateRuleItemReq {

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

    private String scoreMode;

    private String dedupeGroup;

    private BigDecimal coeff;

    private String moduleCode;

    private String submoduleCode;
}
