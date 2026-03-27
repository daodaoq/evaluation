package com.project.evaluation.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.mapper.EvaluationApprovalMapper;
import com.project.evaluation.service.EvaluationApprovalService;
import com.project.evaluation.utils.SecurityContextUtil;
import com.project.evaluation.vo.EvaluationApproval.EvaluationApplyItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EvaluationApprovalServiceImpl implements EvaluationApprovalService {

    @Autowired
    private EvaluationApprovalMapper evaluationApprovalMapper;

    @Override
    public PageBean<EvaluationApplyItemVO> pageApplyItems(Integer pageNum, Integer pageSize,
                                                          String studentNo, Long periodId,
                                                          String applyStatus, String itemStatus,
                                                          Long collegeId, Long classId) {
        try (Page<Object> ignored = PageHelper.startPage(pageNum, pageSize)) {
            List<EvaluationApplyItemVO> rows = evaluationApprovalMapper.pageApplyItems(
                    studentNo, periodId, applyStatus, itemStatus, collegeId, classId
            );
            PageInfo<EvaluationApplyItemVO> pageInfo = new PageInfo<>(rows);
            return new PageBean<>(pageInfo.getTotal(), pageInfo.getList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveApplyItem(Long applyItemId, String remark) {
        if (applyItemId == null || applyItemId <= 0) {
            throw new IllegalArgumentException("非法申报项ID");
        }
        int affected = evaluationApprovalMapper.updateApplyItemStatus(applyItemId, "APPROVED");
        if (affected == 0) {
            throw new IllegalArgumentException("申报项不存在");
        }
        Integer auditorId = SecurityContextUtil.getCurrentUserId();
        evaluationApprovalMapper.insertAuditRecord(applyItemId, auditorId.longValue(), "PASS", remark);
        refreshApplyStatus(applyItemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectApplyItem(Long applyItemId, String remark) {
        if (applyItemId == null || applyItemId <= 0) {
            throw new IllegalArgumentException("非法申报项ID");
        }
        int affected = evaluationApprovalMapper.updateApplyItemStatus(applyItemId, "REJECTED");
        if (affected == 0) {
            throw new IllegalArgumentException("申报项不存在");
        }
        Integer auditorId = SecurityContextUtil.getCurrentUserId();
        evaluationApprovalMapper.insertAuditRecord(applyItemId, auditorId.longValue(), "REJECT", remark);
        refreshApplyStatus(applyItemId);
    }

    private void refreshApplyStatus(Long applyItemId) {
        Long applyId = evaluationApprovalMapper.findApplyIdByApplyItemId(applyItemId);
        if (applyId == null) {
            return;
        }
        int total = evaluationApprovalMapper.countApplyItems(applyId);
        if (total <= 0) {
            return;
        }
        int rejected = evaluationApprovalMapper.countApplyItemsByStatus(applyId, "REJECTED");
        if (rejected > 0) {
            evaluationApprovalMapper.updateApplyStatus(applyId, "REJECTED");
            return;
        }
        int approved = evaluationApprovalMapper.countApplyItemsByStatus(applyId, "APPROVED");
        if (approved == total) {
            evaluationApprovalMapper.updateApplyStatus(applyId, "APPROVED");
        } else {
            evaluationApprovalMapper.updateApplyStatus(applyId, "SUBMITTED");
        }
    }
}
