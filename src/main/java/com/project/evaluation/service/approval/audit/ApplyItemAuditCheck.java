package com.project.evaluation.service.approval.audit;

/**
 * 审批前置校验处理器（责任链节点）。
 */
public interface ApplyItemAuditCheck {
    void check(ApplyItemAuditContext context);
}

