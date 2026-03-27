package com.project.evaluation.vo.Rule;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 添加规则总览请求体
 */
@Data
public class AddRuleReq {

    /**
     * 综测周期 id
     */
    private Integer periodId;

    /**
     * 规则总览名称
     */
    private String ruleName;

    /**
     * 版本号
     */
    private String versionCode;

    /**
     * 是否启用
     */
    private Integer status;

    /**
     * 德育权重（可空，服务端默认 10）
     */
    private BigDecimal moralWeight;

    /**
     * 学业权重（可空，服务端默认 70）
     */
    private BigDecimal academicWeight;

    /**
     * 素质能力权重（可空，服务端默认 20）
     */
    private BigDecimal qualityWeight;
}
