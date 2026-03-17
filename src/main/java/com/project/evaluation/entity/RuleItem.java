package com.project.evaluation.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 综测规则项
 */
@Data
public class RuleItem {

    // TODO: 将非数据库字段添加进来

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

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
