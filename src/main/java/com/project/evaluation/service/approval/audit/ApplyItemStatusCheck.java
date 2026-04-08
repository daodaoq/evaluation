package com.project.evaluation.service.approval.audit;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 校验申报项状态是否允许当前审批动作。
 */
@Component
@Order(25)
public class ApplyItemStatusCheck implements ApplyItemAuditCheck {
    @Override
    public void check(ApplyItemAuditContext context) {
        String status = context.getItemStatus();
        ApplyItemAuditAction action = context.getAction();
        if (status == null || action == null) {
            throw new IllegalArgumentException("申报项状态异常");
        }
        if (action == ApplyItemAuditAction.REOPEN) {
            if (!"REJECTED".equalsIgnoreCase(status)) {
                throw new IllegalStateException("仅已驳回的申报项可重开");
            }
            return;
        }
        if (!"PENDING".equalsIgnoreCase(status)) {
            throw new IllegalStateException("仅待审核的申报项可审批");
        }
    }
}

