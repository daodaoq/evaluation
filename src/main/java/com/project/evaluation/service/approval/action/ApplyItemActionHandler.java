package com.project.evaluation.service.approval.action;

import com.project.evaluation.service.approval.audit.ApplyItemAuditAction;

/**
 * 审批动作处理器（策略接口）。
 */
public interface ApplyItemActionHandler {
    ApplyItemAuditAction action();

    void handle(Long applyItemId, String remark);
}

