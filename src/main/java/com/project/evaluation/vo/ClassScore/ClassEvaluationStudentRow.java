package com.project.evaluation.vo.ClassScore;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClassEvaluationStudentRow {
    private Long userId;
    private String studentNo;
    private String studentName;
    private Long classId;
    private String className;
    private BigDecimal intellectualScore;
}
