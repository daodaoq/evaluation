package com.project.evaluation.vo.ClassScore;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApprovedRuleItemScoreRow {
    private Long studentUserId;
    private String itemName;
    private String moduleCode;
    private String level;
    private BigDecimal baseScore;
    private BigDecimal coeff;
    private String scoreMode;
}
