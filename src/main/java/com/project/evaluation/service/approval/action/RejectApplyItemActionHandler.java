package com.project.evaluation.service.approval.action;

import com.project.evaluation.mapper.EvaluationApprovalMapper;
import com.project.evaluation.service.approval.audit.ApplyItemAuditAction;
import com.project.evaluation.utils.SecurityContextUtil;
import org.springframework.stereotype.Component;

/**
 * 驳回审批处理器。
 */
@Component
public class RejectApplyItemActionHandler implements ApplyItemActionHandler {

    private final EvaluationApprovalMapper evaluationApprovalMapper;

    public RejectApplyItemActionHandler(EvaluationApprovalMapper evaluationApprovalMapper) {
        this.evaluationApprovalMapper = evaluationApprovalMapper;
    }

    @Override
    public ApplyItemAuditAction action() {
        return ApplyItemAuditAction.REJECT;
    }

    @Override
    public void handle(Long applyItemId, String remark) {
        int affected = evaluationApprovalMapper.updateApplyItemStatus(applyItemId, "REJECTED");
        if (affected == 0) {
            throw new IllegalArgumentException("申报项不存在");
        }
        Integer auditorId = SecurityContextUtil.getCurrentUserId();
        evaluationApprovalMapper.insertAuditRecord(applyItemId, auditorId.longValue(), "REJECT", remark);
    }
}

