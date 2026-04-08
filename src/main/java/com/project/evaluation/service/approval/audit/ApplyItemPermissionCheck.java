package com.project.evaluation.service.approval.audit;

import com.project.evaluation.service.PeriodWorkflowService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 按审批动作校验周期权限。
 */
@Component
@Order(30)
public class ApplyItemPermissionCheck implements ApplyItemAuditCheck {

    private final PeriodWorkflowService periodWorkflowService;

    public ApplyItemPermissionCheck(PeriodWorkflowService periodWorkflowService) {
        this.periodWorkflowService = periodWorkflowService;
    }

    @Override
    public void check(ApplyItemAuditContext context) {
        Long periodId = context.getPeriodId();
        if (periodId == null) {
            return;
        }
        if (context.getAction() == ApplyItemAuditAction.REOPEN) {
            periodWorkflowService.assertNotArchivedOnly(periodId);
            return;
        }
        periodWorkflowService.assertTeacherCanAudit(periodId);
    }
}

