package com.project.evaluation.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EvaluationObjection {
    private Long id;
    private Long periodId;
    private Long studentUserId;
    private Integer classId;
    private String content;
    private String status;
    private Long handlerUserId;
    private String handlerRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
