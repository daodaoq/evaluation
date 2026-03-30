package com.project.evaluation.vo.EvaluationApproval;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EvaluationApplyItemVO {
    private Long applyId;
    private Long applyItemId;
    private Long studentId;
    private String studentNo;
    private String studentName;
    private Long collegeId;
    private String collegeName;
    private Long classId;
    private String className;
    private Long periodId;
    private String applyStatus;
    private String itemStatus;
    private Long ruleItemId;
    private String ruleItemName;
    private BigDecimal score;
    private Long auditorId;
    private String auditorNo;
    private String auditorName;
    private LocalDateTime applyCreateTime;
    private LocalDateTime itemCreateTime;
}
