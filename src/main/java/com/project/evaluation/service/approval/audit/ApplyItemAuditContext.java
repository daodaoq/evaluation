package com.project.evaluation.service.approval.audit;

import lombok.Getter;
import lombok.Setter;

/**
 * 审批责任链上下文。
 */
@Getter
@Setter
public class ApplyItemAuditContext {
    private Long applyItemId;
    private Long periodId;
    private ApplyItemAuditAction action;
    private String itemStatus;
    private Integer studentClassId;
}

