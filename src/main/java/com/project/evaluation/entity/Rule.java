package com.project.evaluation.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 综测规则总览类
 */
@Data
public class Rule {

    // TODO: 将非数据库字段添加进来

    /**
     * 规则总览 id
     */
    private Integer id;

    /**
     * 综测周期 id
     */
    private Integer periodId;

    /**
     * 规则总览名称
     */
    private String ruleName;

    /**
     * 规则总览版本
     */
    private String versionCode;

    /**
     * 是否启用
     */
    private Integer status;

    /**
     * 德育权重（细则默认 10%）
     */
    private BigDecimal moralWeight;

    /**
     * 学业权重（细则默认 70%）
     */
    private BigDecimal academicWeight;

    /**
     * 素质能力权重（细则默认 20%）
     */
    private BigDecimal qualityWeight;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
