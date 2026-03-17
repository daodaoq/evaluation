package com.project.evaluation.vo.Rule;

import lombok.Data;

/**
 * 添加规则总览请求体
 */
@Data
public class AddRuleReq {

    /**
     * 综测周期 id
     */
    private String periodId;

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
}
