package com.project.evaluation.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.project.evaluation.entity.EvaluationApply;
import com.project.evaluation.entity.EvaluationApplyItem;
import com.project.evaluation.entity.EvaluationApplyMaterial;
import com.project.evaluation.entity.EvaluationPublicity;
import com.project.evaluation.entity.EvaluationSubmitTip;
import com.project.evaluation.entity.MyUser;
import com.project.evaluation.mapper.StudentApplyMapper;
import com.project.evaluation.mapper.StudentPeriodConfirmMapper;
import com.project.evaluation.mapper.UserMapper;
import com.project.evaluation.service.AcademicScoreService;
import com.project.evaluation.service.EvaluationPublicityService;
import com.project.evaluation.service.EvaluationSubmitTipService;
import com.project.evaluation.service.PeriodEventLogService;
import com.project.evaluation.service.PeriodWorkflowService;
import com.project.evaluation.service.StudentApplyService;
import com.project.evaluation.utils.ApplyItemScoreUtil;
import com.project.evaluation.utils.SecurityContextUtil;
import com.project.evaluation.ws.ApprovalNotifyService;
import com.project.evaluation.vo.StudentApply.ApplyItemReq;
import com.project.evaluation.vo.StudentApply.ApplyMaterialReq;
import com.project.evaluation.vo.StudentApply.MyApplyVO;
import com.project.evaluation.vo.StudentApply.RuleItemSimpleVO;
import com.project.evaluation.vo.AcademicScore.MyAcademicScoreVO;
import com.project.evaluation.vo.StudentApply.StudentApplyApprovedScoreRow;
import com.project.evaluation.vo.StudentApply.StudentPeriodWorkflowVO;
import com.project.evaluation.vo.StudentApply.StudentSectionScoreVO;
import com.project.evaluation.vo.StudentApply.SubmitApplyReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private EvaluationSubmitTipService evaluationSubmitTipService;

    @Autowired
    private AcademicScoreService academicScoreService;

    private static final LinkedHashMap<String, String> SECTION_TITLES = new LinkedHashMap<>();

    static {
        SECTION_TITLES.put("moral", "德育评价");
        SECTION_TITLES.put("academic", "学业水平（智育）");
        SECTION_TITLES.put("quality_bodymind", "身心素养");
        SECTION_TITLES.put("quality_art", "审美与人文素养");
        SECTION_TITLES.put("quality_labor", "劳动素养");
        SECTION_TITLES.put("quality_innovation", "创新素养");
        SECTION_TITLES.put("custom", "其他（非细则项）");
    }

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
        boolean submitNone = Boolean.TRUE.equals(req.getSubmitNone());
        if (CollectionUtils.isEmpty(req.getItems()) && !submitNone) {
            throw new IllegalArgumentException("请至少提交一个申报项，或选择“提交无奖项”");
        }
        if (!CollectionUtils.isEmpty(req.getItems()) && submitNone) {
            throw new IllegalArgumentException("“提交无奖项”与申报项不能同时提交");
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

        if (!CollectionUtils.isEmpty(req.getItems())) {
            for (ApplyItemReq itemReq : req.getItems()) {
                validateAndInsertItem(apply.getId(), itemReq);
            }
        }

        int pendingItemCount = req.getItems() == null ? 0 : req.getItems().size();
        MyUser stu = userMapper.selectById(currentUserId);
        String payload = buildNewApplyNotifyJson(apply.getId(), req.getPeriodId(), stu, pendingItemCount);
        Integer classId = stu != null ? stu.getClassId() : null;
        if (pendingItemCount > 0) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    approvalNotifyService.notifyNewApplyPendingReview(classId, payload);
                }
            });
        }
        if (submitNone) {
            periodEventLogService.log(req.getPeriodId(), "STUDENT_SUBMIT_NONE",
                    "学生提交无奖项 userId=" + currentUserId + ", applyId=" + apply.getId());
        }
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

    @Override
    public List<EvaluationSubmitTip> listSubmitTipsForStudent(Long periodId, String sectionCode) {
        return evaluationSubmitTipService.listForStudent(periodId, sectionCode);
    }

    @Override
    public List<StudentSectionScoreVO> listMySectionScores(Long periodId) {
        if (periodId == null || periodId <= 0) {
            throw new IllegalArgumentException("请选择有效综测周期");
        }
        periodWorkflowService.requirePeriod(periodId);
        Long uid = SecurityContextUtil.getCurrentUserId().longValue();
        Map<String, BigDecimal> sums = new LinkedHashMap<>();
        for (String code : SECTION_TITLES.keySet()) {
            sums.put(code, BigDecimal.ZERO);
        }
        List<StudentApplyApprovedScoreRow> rows = studentApplyMapper.listApprovedScoresForPeriod(uid, periodId);
        for (StudentApplyApprovedScoreRow row : rows) {
            String sec = resolveSectionCode(row.getSourceType(), row.getModuleCode(), row.getSubmoduleCode());
            BigDecimal v = ApplyItemScoreUtil.effectiveScore(
                    row.getScore(),
                    row.getSourceType(),
                    row.getBaseScore(),
                    row.getCoeff(),
                    row.getScoreMode());
            sums.merge(sec, v, BigDecimal::add);
        }
        MyAcademicScoreVO ac = academicScoreService.getMyScore(periodId);
        BigDecimal intellectual = BigDecimal.ZERO;
        if (ac != null && ac.getIntellectualScore() != null) {
            intellectual = ac.getIntellectualScore();
        }
        sums.put("academic", intellectual);

        List<StudentSectionScoreVO> out = new ArrayList<>();
        for (Map.Entry<String, String> e : SECTION_TITLES.entrySet()) {
            StudentSectionScoreVO vo = new StudentSectionScoreVO();
            vo.setSectionCode(e.getKey());
            vo.setSectionTitle(e.getValue());
            vo.setEarnedScore(sums.getOrDefault(e.getKey(), BigDecimal.ZERO));
            out.add(vo);
        }
        return out;
    }

    private static String resolveSectionCode(String sourceType, String moduleCode, String submoduleCode) {
        if (StringUtils.hasText(sourceType) && "CUSTOM".equalsIgnoreCase(sourceType.trim())) {
            return "custom";
        }
        String m = moduleCode == null ? "" : moduleCode.trim().toUpperCase();
        String s = submoduleCode == null ? "" : submoduleCode.trim().toUpperCase();
        if ("MORAL".equals(m)) {
            return "moral";
        }
        if ("ACADEMIC".equals(m)) {
            return "academic";
        }
        if ("QUALITY".equals(m)) {
            if ("BODYMIND".equals(s)) {
                return "quality_bodymind";
            }
            if ("ART".equals(s) || "MEDIA".equals(s) || "ACTIVITY".equals(s)) {
                return "quality_art";
            }
            if ("LABOR".equals(s) || "LANGUAGE".equals(s)) {
                return "quality_labor";
            }
            if ("INNOVATION".equals(s) || "PAPER".equals(s)) {
                return "quality_innovation";
            }
            return "quality_labor";
        }
        if (m.isEmpty()) {
            return "custom";
        }
        return "moral";
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
