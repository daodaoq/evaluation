package com.project.evaluation.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 综测规则总览（绑定周期），与表 evaluation_rule 对应。
 */
@Data
public class Rule {

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

    /** 记录创建时间 */
    private LocalDateTime createTime;
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;
}
