package com.project.evaluation.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EvaluationApplyItem {
    private Long id;
    private Long applyId;
    private Long ruleItemId;
    private BigDecimal score;
    private String status;
    private String sourceType;
    private String customName;
    private String remark;
}
