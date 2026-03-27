package com.project.evaluation.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EvaluationApply {
    private Long id;
    private Long studentId;
    private Long periodId;
    private String status;
    private BigDecimal totalScore;
}
