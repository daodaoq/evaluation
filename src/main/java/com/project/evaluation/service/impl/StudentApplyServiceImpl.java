package com.project.evaluation.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.project.evaluation.entity.EvaluationApply;
import com.project.evaluation.entity.EvaluationApplyItem;
import com.project.evaluation.entity.EvaluationApplyMaterial;
import com.project.evaluation.entity.EvaluationPublicity;
import com.project.evaluation.entity.MyUser;
import com.project.evaluation.mapper.StudentApplyMapper;
import com.project.evaluation.mapper.StudentPeriodConfirmMapper;
import com.project.evaluation.mapper.UserMapper;
import com.project.evaluation.service.EvaluationPublicityService;
import com.project.evaluation.service.PeriodEventLogService;
import com.project.evaluation.service.PeriodWorkflowService;
import com.project.evaluation.service.StudentApplyService;
import com.project.evaluation.utils.SecurityContextUtil;
import com.project.evaluation.ws.ApprovalNotifyService;
import com.project.evaluation.vo.StudentApply.ApplyItemReq;
import com.project.evaluation.vo.StudentApply.ApplyMaterialReq;
import com.project.evaluation.vo.StudentApply.MyApplyVO;
import com.project.evaluation.vo.StudentApply.RuleItemSimpleVO;
import com.project.evaluation.vo.StudentApply.StudentPeriodWorkflowVO;
import com.project.evaluation.vo.StudentApply.SubmitApplyReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StudentApplyServiceImpl implements StudentApplyService {

    @Autowired
    private StudentApplyMapper studentApplyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApprovalNotifyService approvalNotifyService;

    @Autowired
    private PeriodWorkflowService periodWorkflowService;

    @Autowired
    private StudentPeriodConfirmMapper studentPeriodConfirmMapper;

    @Autowired
    private PeriodEventLogService periodEventLogService;

    @Autowired
    private EvaluationPublicityService evaluationPublicityService;

    @Override
    public List<RuleItemSimpleVO> listRuleItems(Long periodId) {
        if (periodId == null || periodId <= 0) {
            throw new IllegalArgumentException("请选择有效综测周期");
        }
        if (studentApplyMapper.countActivePeriod(periodId) == 0) {
            throw new IllegalArgumentException("当前综测周期未启用");
        }
        return studentApplyMapper.listEnabledRuleItemsByPeriod(periodId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitApply(SubmitApplyReq req) {
        if (req == null || req.getPeriodId() == null || req.getPeriodId() <= 0) {
            throw new IllegalArgumentException("请选择有效综测周期");
        }
        if (CollectionUtils.isEmpty(req.getItems())) {
            throw new IllegalArgumentException("请至少提交一个申报项");
        }
        if (studentApplyMapper.countActivePeriod(req.getPeriodId()) == 0) {
            throw new IllegalArgumentException("当前综测周期未启用");
        }

        Integer currentUserId = SecurityContextUtil.getCurrentUserId();
        periodWorkflowService.assertStudentCanSubmit(req.getPeriodId(), currentUserId);
        EvaluationApply apply = new EvaluationApply();
        apply.setStudentId(currentUserId.longValue());
        apply.setPeriodId(req.getPeriodId());
        apply.setStatus("SUBMITTED");
        apply.setTotalScore(BigDecimal.ZERO);
        studentApplyMapper.insertApply(apply);

        for (ApplyItemReq itemReq : req.getItems()) {
            validateAndInsertItem(apply.getId(), itemReq);
        }

        int pendingItemCount = req.getItems().size();
        MyUser stu = userMapper.selectById(currentUserId);
        String payload = buildNewApplyNotifyJson(apply.getId(), req.getPeriodId(), stu, pendingItemCount);
        Integer classId = stu != null ? stu.getClassId() : null;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                approvalNotifyService.notifyNewApplyPendingReview(classId, payload);
            }
        });
    }

    private static String buildNewApplyNotifyJson(Long applyId, Long periodId, MyUser stu, int pendingItemCount) {
        JSONObject o = new JSONObject();
        o.put("type", "NEW_APPLY_PENDING_REVIEW");
        o.put("applyId", applyId);
        o.put("periodId", periodId);
        o.put("pendingItemCount", pendingItemCount);
        o.put("studentName", stu != null && StringUtils.hasText(stu.getRealName()) ? stu.getRealName() : "");
        o.put("studentNo", stu != null && StringUtils.hasText(stu.getStudentId()) ? stu.getStudentId() : "");
        return o.toJSONString();
    }

    @Override
    public List<MyApplyVO> listMyApplyItems() {
        Integer currentUserId = SecurityContextUtil.getCurrentUserId();
        return studentApplyMapper.listMyApplyItems(currentUserId.longValue());
    }

    @Override
    public StudentPeriodWorkflowVO getStudentPeriodWorkflow(Long periodId) {
        if (periodId == null || periodId <= 0) {
            throw new IllegalArgumentException("请选择有效综测周期");
        }
        Integer uid = SecurityContextUtil.getCurrentUserId();
        return periodWorkflowService.buildStudentWorkflowView(periodId, uid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmPeriodNoObjection(Long periodId) {
        if (periodId == null || periodId <= 0) {
            throw new IllegalArgumentException("请选择有效综测周期");
        }
        Integer uid = SecurityContextUtil.getCurrentUserId();
        periodWorkflowService.assertStudentCanConfirm(periodId);
        if (studentPeriodConfirmMapper.exists(uid.longValue(), periodId)) {
            throw new IllegalStateException("您已确认过本周期无异议");
        }
        studentPeriodConfirmMapper.insert(uid.longValue(), periodId);
        periodEventLogService.log(periodId, "STUDENT_CONFIRM", "学生确认无异议 userId=" + uid);
    }

    @Override
    public List<EvaluationPublicity> listActivePublicityForStudent(Long periodId) {
        return evaluationPublicityService.listActiveForCurrentStudent(periodId);
    }

    private void validateAndInsertItem(Long applyId, ApplyItemReq itemReq) {
        if (itemReq == null) {
            throw new IllegalArgumentException("申报项不能为空");
        }
        boolean isRuleItem = itemReq.getRuleItemId() != null && itemReq.getRuleItemId() > 0;
        if (!isRuleItem) {
            // 非细则项：必须备注 + 证明材料
            if (!StringUtils.hasText(itemReq.getCustomName())) {
                throw new IllegalArgumentException("非细则项必须填写申报名称");
            }
            if (!StringUtils.hasText(itemReq.getRemark())) {
                throw new IllegalArgumentException("非细则项必须填写备注说明");
            }
            if (CollectionUtils.isEmpty(itemReq.getMaterials())) {
                throw new IllegalArgumentException("非细则项必须上传证明材料");
            }
        } else {
            String moduleCode = studentApplyMapper.findModuleCodeByRuleItemId(itemReq.getRuleItemId());
            if ("ACADEMIC".equalsIgnoreCase(moduleCode)) {
                throw new IllegalArgumentException("学业水平（智育）由管理端维护，学生端不可申报该项");
            }
            Integer needMaterial = studentApplyMapper.findNeedMaterialByRuleItemId(itemReq.getRuleItemId());
            if (needMaterial == null) {
                throw new IllegalArgumentException("存在无效细则项");
            }
            if (needMaterial == 1 && CollectionUtils.isEmpty(itemReq.getMaterials())) {
                throw new IllegalArgumentException("该细则项必须上传证明材料");
            }
        }

        EvaluationApplyItem item = new EvaluationApplyItem();
        item.setApplyId(applyId);
        item.setRuleItemId(isRuleItem ? itemReq.getRuleItemId() : null);
        item.setScore(BigDecimal.ZERO);
        item.setStatus("PENDING");
        item.setSourceType(isRuleItem ? "RULE" : "CUSTOM");
        item.setCustomName(isRuleItem ? null : itemReq.getCustomName().trim());
        item.setRemark(StringUtils.hasText(itemReq.getRemark()) ? itemReq.getRemark().trim() : null);
        studentApplyMapper.insertApplyItem(item);

        if (!CollectionUtils.isEmpty(itemReq.getMaterials())) {
            for (ApplyMaterialReq materialReq : itemReq.getMaterials()) {
                if (materialReq == null) continue;
                if (!StringUtils.hasText(materialReq.getFileName()) || !StringUtils.hasText(materialReq.getFileUrl())) {
                    throw new IllegalArgumentException("材料文件名和链接不能为空");
                }
                EvaluationApplyMaterial material = new EvaluationApplyMaterial();
                material.setApplyItemId(item.getId());
                material.setFileName(materialReq.getFileName().trim());
                material.setFileUrl(materialReq.getFileUrl().trim());
                studentApplyMapper.insertApplyMaterial(material);
            }
        }
    }
}
