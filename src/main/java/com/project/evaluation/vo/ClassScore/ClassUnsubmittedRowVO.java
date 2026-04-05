package com.project.evaluation.vo.ClassScore;

import lombok.Data;

@Data
public class ClassUnsubmittedRowVO {
    private Long periodId;
    private Long userId;
    private String studentNo;
    private String studentName;
    private Long classId;
    private String className;
}
