package com.project.evaluation.vo.ApplyAppeal;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplyAppealRowVO {
    private Long appealId;
    private Long applyItemId;
    private Long applyId;
    private String studentNo;
    private String studentName;
    private String collegeName;
    private String className;
    private Long periodId;
    private String applyStatus;
    private String itemStatus;
    private String ruleItemName;
    private String customName;
    private String appealReason;
    private String appealStatus;
    private LocalDateTime appealCreateTime;
    private String handlerRemark;
}
