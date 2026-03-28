package com.project.evaluation.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluation.entity.EvaluationObjection;
import com.project.evaluation.entity.MyUser;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.mapper.EvaluationObjectionMapper;
import com.project.evaluation.mapper.UserMapper;
import com.project.evaluation.service.EvaluationObjectionService;
import com.project.evaluation.service.PeriodEventLogService;
import com.project.evaluation.service.PeriodWorkflowService;
import com.project.evaluation.service.TeacherScopeService;
import com.project.evaluation.utils.SecurityContextUtil;
import com.project.evaluation.vo.Objection.HandleObjectionReq;
import com.project.evaluation.vo.Objection.ObjectionRowVO;
import com.project.evaluation.vo.StudentApply.SubmitObjectionReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
public class EvaluationObjectionServiceImpl implements EvaluationObjectionService {

    @Autowired
    private EvaluationObjectionMapper evaluationObjectionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PeriodWorkflowService periodWorkflowService;

    @Autowired
    private TeacherScopeService teacherScopeService;

    @Autowired
    private PeriodEventLogService periodEventLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitByStudent(SubmitObjectionReq req) {
        if (req == null || req.getPeriodId() == null || req.getPeriodId() <= 0) {
            throw new IllegalArgumentException("请选择综测周期");
        }
        String content = req.getContent() == null ? "" : req.getContent().trim();
        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("请填写异议内容");
        }
        if (content.length() > 2000) {
            throw new IllegalArgumentException("异议内容请勿超过2000字");
        }
        Integer uid = SecurityContextUtil.getCurrentUserId();
        periodWorkflowService.assertStudentCanObject(req.getPeriodId(), uid);
        MyUser u = userMapper.selectById(uid);
        EvaluationObjection row = new EvaluationObjection();
        row.setPeriodId(req.getPeriodId());
        row.setStudentUserId(uid.longValue());
        row.setClassId(u != null ? u.getClassId() : null);
        row.setContent(content);
        evaluationObjectionMapper.insert(row);
        periodEventLogService.log(req.getPeriodId(), "OBJECTION_SUBMIT", "学生提交异议 id=" + row.getId());
    }

    @Override
    public PageBean<ObjectionRowVO> page(Integer pageNum, Integer pageSize, Long periodId, String status, Long classId) {
        ReviewScope scope = resolveScope(classId);
        if (scope.emptyResult()) {
            return new PageBean<>(0L, Collections.emptyList());
        }
        try (Page<Object> ignored = PageHelper.startPage(pageNum, pageSize)) {
            List<ObjectionRowVO> rows = evaluationObjectionMapper.pageRows(periodId, status, classId, scope.classIdsFilter());
            PageInfo<ObjectionRowVO> info = new PageInfo<>(rows);
            return new PageBean<>(info.getTotal(), info.getList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(HandleObjectionReq req) {
        if (req == null || req.getObjectionId() == null) {
            throw new IllegalArgumentException("非法异议ID");
        }
        String decision = req.getDecision() == null ? "" : req.getDecision().trim().toUpperCase();
        if (!"HANDLED".equals(decision) && !"REJECTED".equals(decision)) {
            throw new IllegalArgumentException("处理结果须为 HANDLED 或 REJECTED");
        }
        String remark = req.getHandlerRemark() == null ? "" : req.getHandlerRemark().trim();
        if (remark.length() > 500) {
            throw new IllegalArgumentException("处理说明请勿超过500字");
        }
        EvaluationObjection row = evaluationObjectionMapper.selectById(req.getObjectionId());
        if (row == null) {
            throw new IllegalArgumentException("异议不存在");
        }
        if (!"PENDING".equalsIgnoreCase(row.getStatus())) {
            throw new IllegalArgumentException("该异议已处理");
        }
        if (row.getStudentUserId() == null) {
            throw new IllegalStateException("异议数据异常");
        }
        teacherScopeService.assertCanOperateStudentUser(row.getStudentUserId().intValue());
        periodWorkflowService.assertNotArchivedOnly(row.getPeriodId());

        Integer handlerId = SecurityContextUtil.getCurrentUserId();
        int n = evaluationObjectionMapper.updateHandled(req.getObjectionId(), decision, handlerId.longValue(),
                StringUtils.hasText(remark) ? remark : null);
        if (n == 0) {
            throw new IllegalStateException("异议状态已变更，请刷新后重试");
        }
        periodEventLogService.log(row.getPeriodId(), "OBJECTION_HANDLE", "异议处理 id=" + req.getObjectionId() + " -> " + decision);
    }

    private ReviewScope resolveScope(Long classId) {
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
        if (classId != null && !managed.contains(classId.intValue())) {
            throw new IllegalArgumentException("您无权查看该班级数据");
        }
        return new ReviewScope(managed, false);
    }

    private record ReviewScope(List<Integer> classIdsFilter, boolean emptyResult) {}
}
