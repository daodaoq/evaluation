package com.project.evaluation.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EvaluationPublicity {
    private Long id;
    private Long periodId;
    private Long classId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
