package com.project.evaluation.vo.Rule;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新规则总览请求体
 */
@Data
public class UpdateRuleReq {

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

    private BigDecimal moralWeight;

    private BigDecimal academicWeight;

    private BigDecimal qualityWeight;
}
