package com.project.evaluation.vo.EvaluationApproval;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplyItemScoringSnapshot {
    private String sourceType;
    private Long ruleItemId;
    private BigDecimal baseScore;
    private BigDecimal coeff;
    private String scoreMode;
}
