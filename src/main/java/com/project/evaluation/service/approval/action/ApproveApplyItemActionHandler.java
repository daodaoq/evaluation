package com.project.evaluation.service.approval.action;

import com.project.evaluation.mapper.EvaluationApprovalMapper;
import com.project.evaluation.service.approval.audit.ApplyItemAuditAction;
import com.project.evaluation.utils.ApplyItemScoreUtil;
import com.project.evaluation.utils.SecurityContextUtil;
import com.project.evaluation.vo.EvaluationApproval.ApplyItemScoringSnapshot;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 通过审批处理器。
 */
@Component
public class ApproveApplyItemActionHandler implements ApplyItemActionHandler {

    private final EvaluationApprovalMapper evaluationApprovalMapper;

    public ApproveApplyItemActionHandler(EvaluationApprovalMapper evaluationApprovalMapper) {
        this.evaluationApprovalMapper = evaluationApprovalMapper;
    }

    @Override
    public ApplyItemAuditAction action() {
        return ApplyItemAuditAction.APPROVE;
    }

    @Override
    public void handle(Long applyItemId, String remark) {
        ApplyItemScoringSnapshot snap = evaluationApprovalMapper.selectScoringSnapshot(applyItemId);
        BigDecimal persisted =
                snap != null && snap.getPersistedScore() != null ? snap.getPersistedScore() : BigDecimal.ZERO;
        BigDecimal score = ApplyItemScoreUtil.effectiveScore(
                persisted,
                snap != null ? snap.getSourceType() : null,
                snap != null ? snap.getBaseScore() : null,
                snap != null ? snap.getCoeff() : null,
                snap != null ? snap.getScoreMode() : null);
        int affected = evaluationApprovalMapper.updateApplyItemStatusAndScore(applyItemId, "APPROVED", score);
        if (affected == 0) {
            throw new IllegalArgumentException("申报项不存在");
        }
        Integer auditorId = SecurityContextUtil.getCurrentUserId();
        evaluationApprovalMapper.insertAuditRecord(applyItemId, auditorId.longValue(), "PASS", remark);
    }
}

