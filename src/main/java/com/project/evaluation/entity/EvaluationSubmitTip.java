package com.project.evaluation.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EvaluationSubmitTip {
    private Long id;
    private Long periodId;
    private String sectionCode;
    private String title;
    private String content;
    private Integer sortOrder;
    private Integer status;
    private Integer operatorUserId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
