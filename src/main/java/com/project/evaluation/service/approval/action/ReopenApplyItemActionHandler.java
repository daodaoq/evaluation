package com.project.evaluation.service.approval.action;

import com.project.evaluation.mapper.EvaluationApprovalMapper;
import com.project.evaluation.service.approval.audit.ApplyItemAuditAction;
import org.springframework.stereotype.Component;

/**
 * 重开申报项处理器。
 */
@Component
public class ReopenApplyItemActionHandler implements ApplyItemActionHandler {

    private final EvaluationApprovalMapper evaluationApprovalMapper;

    public ReopenApplyItemActionHandler(EvaluationApprovalMapper evaluationApprovalMapper) {
        this.evaluationApprovalMapper = evaluationApprovalMapper;
    }

    @Override
    public ApplyItemAuditAction action() {
        return ApplyItemAuditAction.REOPEN;
    }

    @Override
    public void handle(Long applyItemId, String remark) {
        int affected = evaluationApprovalMapper.updateApplyItemStatus(applyItemId, "PENDING");
        if (affected == 0) {
            throw new IllegalArgumentException("申报项不存在");
        }
    }
}

