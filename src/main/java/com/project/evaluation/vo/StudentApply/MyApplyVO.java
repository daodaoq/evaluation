package com.project.evaluation.vo.StudentApply;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MyApplyVO {
    private Long applyId;
    private Long periodId;
    private String applyStatus;
    private BigDecimal totalScore;
    private LocalDateTime createTime;
    private Long applyItemId;
    private String itemStatus;
    private Long ruleItemId;
    private String itemName;
    private String sourceType;
    private String customName;
    private String remark;
}
