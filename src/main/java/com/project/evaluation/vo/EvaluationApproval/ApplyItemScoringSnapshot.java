package com.project.evaluation.vo.EvaluationApproval;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplyItemScoringSnapshot {
    /** 申报项 id；单条查询与批量查询均填充，供列表端组装 */
    private Long applyItemId;
    private String sourceType;
    private Long ruleItemId;
    /** 学生提交时已写入的分值（任职分等），审批通过时参与 effectiveScore */
    private BigDecimal persistedScore;
    private BigDecimal baseScore;
    private BigDecimal coeff;
    private String scoreMode;
}
