package com.project.evaluation.service.approval.audit;

import com.project.evaluation.mapper.EvaluationApprovalMapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 校验申报项存在，并解析所在周期。
 */
@Component
@Order(20)
public class ApplyItemExistsCheck implements ApplyItemAuditCheck {

    private final EvaluationApprovalMapper evaluationApprovalMapper;

    public ApplyItemExistsCheck(EvaluationApprovalMapper evaluationApprovalMapper) {
        this.evaluationApprovalMapper = evaluationApprovalMapper;
    }

    @Override
    public void check(ApplyItemAuditContext context) {
        Long applyItemId = context.getApplyItemId();
        Long periodId = evaluationApprovalMapper.findPeriodIdByApplyItemId(applyItemId);
        if (periodId == null) {
            throw new IllegalArgumentException("申报项不存在");
        }
        context.setPeriodId(periodId);
        context.setItemStatus(evaluationApprovalMapper.findApplyItemStatusById(applyItemId));
        context.setStudentClassId(evaluationApprovalMapper.findStudentClassIdByApplyItemId(applyItemId));
    }
}

