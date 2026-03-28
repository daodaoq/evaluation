package com.project.evaluation.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluation.entity.EvaluationApplyItemAppeal;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.mapper.ApplyAppealMapper;
import com.project.evaluation.service.ApplyAppealService;
import com.project.evaluation.service.EvaluationApprovalService;
import com.project.evaluation.service.PeriodWorkflowService;
import com.project.evaluation.service.TeacherScopeService;
import com.project.evaluation.utils.SecurityContextUtil;
import com.project.evaluation.vo.ApplyAppeal.ApplyAppealRowVO;
import com.project.evaluation.vo.ApplyAppeal.ApplyItemOwnerRow;
import com.project.evaluation.vo.ApplyAppeal.HandleAppealReq;
import com.project.evaluation.vo.ApplyAppeal.SubmitAppealReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
public class ApplyAppealServiceImpl implements ApplyAppealService {

    @Autowired
    private ApplyAppealMapper applyAppealMapper;

    @Autowired
    private EvaluationApprovalService evaluationApprovalService;

    @Autowired
    private TeacherScopeService teacherScopeService;

    @Autowired
    private PeriodWorkflowService periodWorkflowService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitByStudent(SubmitAppealReq req) {
        if (req == null || req.getApplyItemId() == null || req.getApplyItemId() <= 0) {
            throw new IllegalArgumentException("请选择有效的申报项");
        }
        String reason = req.getReason() == null ? "" : req.getReason().trim();
        if (!StringUtils.hasText(reason)) {
            throw new IllegalArgumentException("请填写申诉理由");
        }
        if (reason.length() > 1000) {
            throw new IllegalArgumentException("申诉理由请勿超过1000字");
        }
        Integer uid = SecurityContextUtil.getCurrentUserId();
        ApplyItemOwnerRow owner = applyAppealMapper.selectItemOwner(req.getApplyItemId());
        if (owner == null || owner.getStudentId() == null) {
            throw new IllegalArgumentException("申报项不存在");
        }
        if (!owner.getStudentId().equals(uid.longValue())) {
            throw new IllegalArgumentException("只能对自己的申报项发起申诉");
        }
        if (!"REJECTED".equalsIgnoreCase(owner.getItemStatus())) {
            throw new IllegalArgumentException("仅「已驳回」的申报项可申诉");
        }
        Long periodId = applyAppealMapper.findPeriodIdByApplyItemId(req.getApplyItemId());
        if (periodId != null) {
            periodWorkflowService.assertStudentCanAppeal(periodId, uid);
        }
        if (applyAppealMapper.countPendingByApplyItemId(req.getApplyItemId()) > 0) {
            throw new IllegalArgumentException("该申报项已有待处理的申诉");
        }
        EvaluationApplyItemAppeal row = new EvaluationApplyItemAppeal();
        row.setApplyItemId(req.getApplyItemId());
        row.setStudentId(owner.getStudentId());
        row.setReason(reason);
        row.setStatus("PENDING");
        applyAppealMapper.insert(row);
    }

    @Override
    public PageBean<ApplyAppealRowVO> pageAppeals(Integer pageNum, Integer pageSize,
                                                  String studentNo, Long periodId, String appealStatus,
                                                  Long collegeId, Long classId) {
        ReviewScope scope = resolveReviewScope(classId);
        if (scope.emptyResult()) {
            return new PageBean<>(0L, Collections.emptyList());
        }
        try (Page<Object> ignored = PageHelper.startPage(pageNum, pageSize)) {
            List<ApplyAppealRowVO> rows = applyAppealMapper.pageAppeals(
                    studentNo, periodId, appealStatus, collegeId, classId, scope.classIdsFilter());
            PageInfo<ApplyAppealRowVO> pageInfo = new PageInfo<>(rows);
            return new PageBean<>(pageInfo.getTotal(), pageInfo.getList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleAppeal(HandleAppealReq req) {
        if (req == null || req.getAppealId() == null || req.getAppealId() <= 0) {
            throw new IllegalArgumentException("非法申诉ID");
        }
        String decision = req.getDecision() == null ? "" : req.getDecision().trim().toUpperCase();
        if (!"ACCEPTED".equals(decision) && !"REJECTED".equals(decision)) {
            throw new IllegalArgumentException("处理结果须为 ACCEPTED 或 REJECTED");
        }
        String remark = req.getHandlerRemark() == null ? "" : req.getHandlerRemark().trim();
        if (remark.length() > 500) {
            throw new IllegalArgumentException("处理说明请勿超过500字");
        }
        EvaluationApplyItemAppeal ap = applyAppealMapper.selectById(req.getAppealId());
        if (ap == null) {
            throw new IllegalArgumentException("申诉不存在");
        }
        if (!"PENDING".equalsIgnoreCase(ap.getStatus())) {
            throw new IllegalArgumentException("该申诉已处理");
        }
        Long periodId = applyAppealMapper.findPeriodIdByApplyItemId(ap.getApplyItemId());
        if (periodId != null) {
            periodWorkflowService.assertNotArchivedOnly(periodId);
        }
        teacherScopeService.assertCanOperateStudentUser(ap.getStudentId().intValue());
        Integer handlerId = SecurityContextUtil.getCurrentUserId();
        int n = applyAppealMapper.updateHandled(req.getAppealId(), decision, handlerId.longValue(),
                StringUtils.hasText(remark) ? remark : null);
        if (n == 0) {
            throw new IllegalStateException("申诉状态已变更，请刷新后重试");
        }
        if ("ACCEPTED".equals(decision)) {
            evaluationApprovalService.reopenApplyItem(ap.getApplyItemId());
        }
    }

    private ReviewScope resolveReviewScope(Long classId) {
        TeacherScopeService.StudentMenuScope scope = teacherScopeService.resolveStudentMenuScope();
        if (scope == TeacherScopeService.StudentMenuScope.DENIED) {
            throw new AccessDeniedException("无权限");
        }
        if (scope == TeacherScopeService.StudentMenuScope.ADMIN) {
            return new ReviewScope(null, false);
        }
        List<Integer> managed = teacherScopeService.getManagedClassIdsForCurrentTeacher();
        if (managed == null || managed.isEmpty()) {
            return new ReviewScope(null, true);
        }
        if (classId != null) {
            if (!managed.contains(classId.intValue())) {
                throw new IllegalArgumentException("您无权查看该班级数据");
            }
        }
        return new ReviewScope(managed, false);
    }

    private record ReviewScope(List<Integer> classIdsFilter, boolean emptyResult) {}
}
