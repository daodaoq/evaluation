package com.project.evaluation.service.approval.audit;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 校验申报项 ID 合法性。
 */
@Component
@Order(10)
public class ApplyItemIdCheck implements ApplyItemAuditCheck {
    @Override
    public void check(ApplyItemAuditContext context) {
        Long applyItemId = context.getApplyItemId();
        if (applyItemId == null || applyItemId <= 0) {
            throw new IllegalArgumentException("非法申报项ID");
        }
    }
}

