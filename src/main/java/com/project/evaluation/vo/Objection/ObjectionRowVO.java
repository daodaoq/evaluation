package com.project.evaluation.vo.Objection;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ObjectionRowVO {
    private Long id;
    private Long periodId;
    private Long studentUserId;
    private String studentNo;
    private String studentName;
    private String className;
    private String content;
    private String status;
    private String handlerRemark;
    private LocalDateTime createTime;
}
