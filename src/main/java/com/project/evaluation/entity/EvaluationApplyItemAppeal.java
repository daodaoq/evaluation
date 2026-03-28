package com.project.evaluation.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EvaluationApplyItemAppeal {
    private Long id;
    private Long applyItemId;
    private Long studentId;
    private String reason;
    private String status;
    private Long handlerId;
    private String handlerRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
