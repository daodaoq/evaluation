package com.project.evaluation.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AcademicScore {
    private Long id;
    private Long periodId;
    private String studentNo;
    private String className;
    private String studentName;
    private BigDecimal intellectualScore;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
