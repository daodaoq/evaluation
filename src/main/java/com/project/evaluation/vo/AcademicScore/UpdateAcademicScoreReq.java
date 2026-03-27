package com.project.evaluation.vo.AcademicScore;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateAcademicScoreReq {
    private Long periodId;
    private String studentNo;
    private String className;
    private String studentName;
    private BigDecimal intellectualScore;
}
