package com.project.evaluation.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.project.evaluation.entity.EvaluationApply;
import com.project.evaluation.entity.EvaluationApplyItem;
import com.project.evaluation.entity.EvaluationApplyMaterial;
import com.project.evaluation.entity.EvaluationPublicity;
import com.project.evaluation.entity.EvaluationSubmitTip;
import com.project.evaluation.entity.MyUser;
import com.project.evaluation.entity.Rule;
import com.project.evaluation.entity.RuleCategory;
import com.project.evaluation.mapper.RuleCategoryMapper;
import com.project.evaluation.mapper.RuleMapper;
import com.project.evaluation.mapper.StudentApplyMapper;
import com.project.evaluation.mapper.StudentPeriodConfirmMapper;
import com.project.evaluation.mapper.UserMapper;
import com.project.evaluation.service.AcademicScoreService;
import com.project.evaluation.service.EvaluationPublicityService;
import com.project.evaluation.service.EvaluationSubmitTipService;
import com.project.evaluation.service.PeriodEventLogService;
import com.project.evaluation.service.PeriodWorkflowService;
import com.project.evaluation.scorepolicy.ScorePolicySnapshot;
import com.project.evaluation.constant.ApplyScoreConstants;
import com.project.evaluation.service.StudentApplyService;
import com.project.evaluation.utils.ApplyItemScoreUtil;
import com.project.evaluation.utils.CategoryUnitScoreCalculator;
import com.project.evaluation.utils.ComprehensiveScoreCalculator;
import com.project.evaluation.utils.MinioUtil;
import com.project.evaluation.utils.SecurityContextUtil;
import com.project.evaluation.ws.ApprovalNotifyService;
import com.project.evaluation.vo.StudentApply.ApplyItemReq;
import com.project.evaluation.vo.StudentApply.ApplyMaterialReq;
import com.project.evaluation.vo.StudentApply.MaterialUploadVO;
import com.project.evaluation.vo.StudentApply.MyApplyVO;
import com.project.evaluation.vo.StudentApply.RuleItemScoreMeta;
import com.project.evaluation.vo.StudentApply.RuleItemSimpleVO;
import com.project.evaluation.vo.AcademicScore.MyAcademicScoreVO;
import com.project.evaluation.vo.StudentApply.StudentApplyApprovedScoreRow;
import com.project.evaluation.vo.StudentApply.StudentCategoryScoreNodeVO;
import com.project.evaluation.vo.StudentApply.StudentCategoryScoreOverviewVO;
import com.project.evaluation.vo.StudentApply.StudentPeriodWorkflowVO;
import com.project.evaluation.vo.StudentApply.StudentRuleCategoryTreeNodeVO;
import com.project.evaluation.vo.StudentApply.StudentScoreExtraRowVO;
import com.project.evaluation.vo.StudentApply.StudentSectionScoreVO;
import com.project.evaluation.vo.StudentApply.SubmitApplyReq;
import com.project.evaluation.vo.RuleCategory.AddRuleCategoryReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private RuleMapper ruleMapper;

    @Autowired
    private RuleCategoryMapper ruleCategoryMapper;

    private static final Set<String> MATERIAL_ALLOWED_EXT = Set.of(
            "pdf", "png", "jpg", "jpeg", "gif", "webp", "doc", "docx", "xls", "xlsx", "zip", "txt");

    @Override
    public List<RuleItemSimpleVO> listRuleItems(Long periodId) {
        if (periodId == null || periodId <= 0) {
            throw new IllegalArgumentException("请选择有效综测周期");
        }
        if (studentApplyMapper.countActivePeriod(periodId) == 0) {
            throw new IllegalArgumentException("当前综测周期未启用");
        }
        List<RuleItemSimpleVO> raw = studentApplyMapper.listEnabledRuleItemsByPeriod(periodId);
        Rule rule = ruleMapper.findLatestByPeriodId(periodId.intValue());
        if (rule == null || rule.getId() == null) {
            return raw;
        }
        List<RuleCategory> cats = ruleCategoryMapper.listByRuleId(rule.getId());
        if (cats == null || cats.isEmpty()) {
            return raw;
        }
        Map<Integer, RuleCategory> byId = cats.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(RuleCategory::getId, c -> c, (a, b) -> a));
        return raw.stream()
                .filter(v -> itemCategoryVisibleForStudent(v.getItemCategory(), byId))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentRuleCategoryTreeNodeVO> listRuleItemCategoryTree(Long periodId) {
        if (periodId == null || periodId <= 0) {
            throw new IllegalArgumentException("请选择有效综测周期");
        }
        if (studentApplyMapper.countActivePeriod(periodId) == 0) {
            throw new IllegalArgumentException("当前综测周期未启用");
        }
        Rule rule = ruleMapper.findLatestByPeriodId(periodId.intValue());
        if (rule == null || rule.getId() == null) {
            return List.of();
        }
        ensurePositionRuleCategoryExists(rule.getId());
        List<RuleCategory> cats = ruleCategoryMapper.listByRuleId(rule.getId());
        Map<Integer, RuleCategory> byId = (cats == null || cats.isEmpty())
                ? Map.of()
                : cats.stream()
                        .filter(c -> c.getId() != null)
                        .collect(Collectors.toMap(RuleCategory::getId, c -> c, (a, b) -> a));
        List<RuleItemSimpleVO> items = studentApplyMapper.listEnabledRuleItemsByPeriod(periodId).stream()
                .filter(v -> itemCategoryVisibleForStudent(v.getItemCategory(), byId))
                .collect(Collectors.toList());
        if (cats == null || cats.isEmpty()) {
            if (items.stream().noneMatch(i -> i.getItemCategory() == null)) {
                return List.of();
            }
            StudentRuleCategoryTreeNodeVO bucket = new StudentRuleCategoryTreeNodeVO();
            bucket.setCategoryName("未分类");
            bucket.setSortOrder(100_000);
            bucket.setItems(new ArrayList<>(items.stream().filter(i -> i.getItemCategory() == null).toList()));
            return List.of(bucket);
        }
        List<RuleCategory> visibleCats = cats.stream()
                .filter(c -> c.getId() != null && itemCategoryVisibleForStudent(c.getId(), byId))
                .sorted(Comparator
                        .comparing(RuleCategory::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(RuleCategory::getId))
                .collect(Collectors.toList());
        Map<Integer, StudentRuleCategoryTreeNodeVO> nodes = new LinkedHashMap<>();
        for (RuleCategory c : visibleCats) {
            StudentRuleCategoryTreeNodeVO n = new StudentRuleCategoryTreeNodeVO();
            n.setId(c.getId());
            n.setParentId(c.getParentId());
            n.setCategoryName(c.getCategoryName());
            n.setSortOrder(c.getSortOrder() != null ? c.getSortOrder() : 0);
            n.setScoreCap(c.getScoreCap());
            n.setChildren(new ArrayList<>());
            n.setItems(new ArrayList<>());
            nodes.put(c.getId(), n);
        }
        for (RuleItemSimpleVO it : items) {
            Integer ic = it.getItemCategory();
            if (ic == null) {
                continue;
            }
            StudentRuleCategoryTreeNodeVO node = nodes.get(ic);
            if (node != null) {
                node.getItems().add(it);
            }
        }
        List<StudentRuleCategoryTreeNodeVO> roots = new ArrayList<>();
        for (StudentRuleCategoryTreeNodeVO n : nodes.values()) {
            int p = n.getParentId() == null || n.getParentId() == 0 ? 0 : n.getParentId();
            if (p == 0) {
                roots.add(n);
            } else {
                StudentRuleCategoryTreeNodeVO parent = nodes.get(p);
                if (parent != null) {
                    parent.getChildren().add(n);
                } else {
                    roots.add(n);
                }
            }
        }
        sortCategoryTreeLevel(roots);
        List<RuleItemSimpleVO> uncategorized = items.stream()
                .filter(i -> i.getItemCategory() == null)
                .collect(Collectors.toList());
        if (!uncategorized.isEmpty()) {
            StudentRuleCategoryTreeNodeVO bucket = new StudentRuleCategoryTreeNodeVO();
            bucket.setCategoryName("未分类");
            bucket.setSortOrder(100_000);
            bucket.setItems(new ArrayList<>(uncategorized));
            bucket.setChildren(new ArrayList<>());
            roots.add(bucket);
        }
        roots.sort(Comparator
                .comparing(StudentRuleCategoryTreeNodeVO::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(n -> n.getId() == null ? Integer.MAX_VALUE : n.getId()));
        pruneEmptyCategoryBranches(roots);
        return roots;
    }

    private static boolean itemCategoryVisibleForStudent(Integer itemCategory, Map<Integer, RuleCategory> byId) {
        if (itemCategory == null) {
            return true;
        }
        RuleCategory x = byId.get(itemCategory);
        if (x == null) {
            return false;
        }
        while (x != null) {
            if (x.getStudentVisible() == null || x.getStudentVisible() == 0) {
                return false;
            }
            int p = x.getParentId() == null || x.getParentId() == 0 ? 0 : x.getParentId();
            if (p == 0) {
                return true;
            }
            x = byId.get(p);
            if (x == null) {
                return false;
            }
        }
        return false;
    }

    private static void sortCategoryTreeLevel(List<StudentRuleCategoryTreeNodeVO> level) {
        if (level == null || level.isEmpty()) {
            return;
        }
        level.sort(Comparator
                .comparing(StudentRuleCategoryTreeNodeVO::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(n -> n.getId() == null ? 0 : n.getId()));
        for (StudentRuleCategoryTreeNodeVO n : level) {
            sortCategoryTreeLevel(n.getChildren());
        }
    }

    private static void pruneEmptyCategoryBranches(List<StudentRuleCategoryTreeNodeVO> level) {
        if (level == null) {
            return;
        }
        Iterator<StudentRuleCategoryTreeNodeVO> it = level.iterator();
        while (it.hasNext()) {
            StudentRuleCategoryTreeNodeVO n = it.next();
            pruneEmptyCategoryBranches(n.getChildren());
            boolean hasItems = n.getItems() != null && !n.getItems().isEmpty();
            boolean hasKids = n.getChildren() != null && !n.getChildren().isEmpty();
            if (!hasItems && !hasKids) {
                if (ApplyScoreConstants.isPositionAnchorCategoryName(n.getCategoryName())) {
                    continue;
                }
                it.remove();
            }
        }
    }

    /**
     * 每条启用规则下保证存在「任职」或「任职分」锚点分类之一；无则自动插入根级「任职」（供任职分自填归集与学生端展示）。
     */
    private void ensurePositionRuleCategoryExists(Integer ruleId) {
        if (ruleId == null) {
            return;
        }
        List<RuleCategory> cats = ruleCategoryMapper.listByRuleId(ruleId);
        if (cats != null) {
            for (RuleCategory c : cats) {
                if (c != null && ApplyScoreConstants.isPositionAnchorCategoryName(c.getCategoryName())) {
                    return;
                }
            }
        }
        AddRuleCategoryReq add = new AddRuleCategoryReq();
        add.setRuleId(ruleId);
        add.setCategoryName(ApplyScoreConstants.POSITION_CATEGORY_NAME);
        add.setParentId(0);
        add.setScoreCap(null);
        add.setStudentVisible(1);
        add.setSortOrder(-50);
        add.setCategoryBaseScore(BigDecimal.ZERO);
        ruleCategoryMapper.addRuleCategory(add);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitApply(SubmitApplyReq req) {
        if (req == null || req.getPeriodId() == null || req.getPeriodId() <= 0) {
            throw new IllegalArgumentException("请选择有效综测周期");
        }
        boolean submitNone = Boolean.TRUE.equals(req.getSubmitNone());
        boolean submitPosNone = Boolean.TRUE.equals(req.getSubmitPositionNone());
        if (submitNone && submitPosNone) {
            throw new IllegalArgumentException("请勿在同一请求中同时提交「本分类无奖项」与「无任职分可报」");
        }
        if (CollectionUtils.isEmpty(req.getItems()) && !submitNone && !submitPosNone) {
            throw new IllegalArgumentException("请至少提交一条申报，或使用「本分类无奖项」「无任职分可报」声明");
        }
        if (!CollectionUtils.isEmpty(req.getItems()) && (submitNone || submitPosNone)) {
            throw new IllegalArgumentException("「无申报」类提交不能与具体申报项在同一请求中提交");
        }
        if (submitNone) {
            if (req.getRuleCategoryId() == null || req.getRuleCategoryId() <= 0) {
                throw new IllegalArgumentException("「本分类无奖项」须指定规则分类 id");
            }
        }
        if (!CollectionUtils.isEmpty(req.getItems())) {
            long positionRows = req.getItems().stream()
                    .filter(r -> r != null && ApplyScoreConstants.isPositionScoreCustomName(r.getCustomName()))
                    .count();
            if (positionRows > 1) {
                throw new IllegalArgumentException("任职分仅能填写一条，请合并为一项提交");
            }
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

        if (submitNone) {
            assertCategoryEligibleForSubmitNone(req.getPeriodId(), req.getRuleCategoryId());
            insertCategorySubmitNoneMarker(apply.getId(), req.getRuleCategoryId());
        } else if (submitPosNone) {
            insertPositionSubmitNoneMarker(apply.getId());
        } else if (!CollectionUtils.isEmpty(req.getItems())) {
            for (ApplyItemReq itemReq : req.getItems()) {
                int q = expandQuantityForItem(itemReq);
                for (int i = 0; i < q; i++) {
                    validateAndInsertItem(apply.getId(), itemReq, currentUserId, req.getPeriodId());
                }
            }
        }

        int pendingItemCount =
                (submitNone || submitPosNone)
                        ? 1
                        : sumSubmitItemQuantities(req.getItems());
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
            periodEventLogService.log(req.getPeriodId(), "STUDENT_SUBMIT_CATEGORY_NONE",
                    "学生提交本分类无奖项 userId=" + currentUserId + ", applyId=" + apply.getId()
                            + ", ruleCategoryId=" + req.getRuleCategoryId());
        } else if (submitPosNone) {
            periodEventLogService.log(req.getPeriodId(), "STUDENT_SUBMIT_POSITION_NONE",
                    "学生提交任职分无申报 userId=" + currentUserId + ", applyId=" + apply.getId());
        }
    }

    private void assertCategoryEligibleForSubmitNone(Long periodId, Integer ruleCategoryId) {
        List<StudentRuleCategoryTreeNodeVO> tree = listRuleItemCategoryTree(periodId);
        StudentRuleCategoryTreeNodeVO node = findCategoryNodeInTree(tree, ruleCategoryId);
        if (node == null) {
            throw new IllegalArgumentException("规则分类不存在、已隐藏或当前周期不可用");
        }
        if (ApplyScoreConstants.isPositionAnchorCategoryName(node.getCategoryName())) {
            return;
        }
        if (!subtreeHasVisibleRuleItems(node)) {
            throw new IllegalArgumentException("该分类下无可申报细则，无需使用「本分类无奖项」");
        }
    }

    private static StudentRuleCategoryTreeNodeVO findCategoryNodeInTree(
            List<StudentRuleCategoryTreeNodeVO> roots, Integer id) {
        if (roots == null || id == null) {
            return null;
        }
        for (StudentRuleCategoryTreeNodeVO n : roots) {
            if (id.equals(n.getId())) {
                return n;
            }
            StudentRuleCategoryTreeNodeVO f = findCategoryNodeInTree(n.getChildren(), id);
            if (f != null) {
                return f;
            }
        }
        return null;
    }

    private static boolean subtreeHasVisibleRuleItems(StudentRuleCategoryTreeNodeVO n) {
        if (n == null) {
            return false;
        }
        if (n.getItems() != null && !n.getItems().isEmpty()) {
            return true;
        }
        if (n.getChildren() == null) {
            return false;
        }
        for (StudentRuleCategoryTreeNodeVO c : n.getChildren()) {
            if (subtreeHasVisibleRuleItems(c)) {
                return true;
            }
        }
        return false;
    }

    private void insertCategorySubmitNoneMarker(Long applyId, Integer ruleCategoryId) {
        EvaluationApplyItem item = new EvaluationApplyItem();
        item.setApplyId(applyId);
        item.setRuleItemId(null);
        item.setScore(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        item.setStatus("PENDING");
        item.setSourceType("CUSTOM");
        item.setCustomName(ApplyScoreConstants.CATEGORY_SUBMIT_NONE_CUSTOM_NAME);
        item.setRemark("RULE_CATEGORY_ID:" + ruleCategoryId);
        studentApplyMapper.insertApplyItem(item);
    }

    private void insertPositionSubmitNoneMarker(Long applyId) {
        EvaluationApplyItem item = new EvaluationApplyItem();
        item.setApplyId(applyId);
        item.setRuleItemId(null);
        item.setScore(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        item.setStatus("PENDING");
        item.setSourceType("CUSTOM");
        item.setCustomName(ApplyScoreConstants.POSITION_SUBMIT_NONE_CUSTOM_NAME);
        item.setRemark("POSITION_SUBMIT_NONE");
        studentApplyMapper.insertApplyItem(item);
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
        List<MyApplyVO> list = studentApplyMapper.listMyApplyItems(currentUserId.longValue());
        if (list.isEmpty()) {
            return list;
        }
        List<Long> itemIds = list.stream()
                .map(MyApplyVO::getApplyItemId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (itemIds.isEmpty()) {
            return list;
        }
        List<EvaluationApplyMaterial> mats = studentApplyMapper.listMaterialsByApplyItemIds(itemIds);
        Map<Long, List<ApplyMaterialReq>> byItem = new LinkedHashMap<>();
        for (EvaluationApplyMaterial m : mats) {
            if (m.getApplyItemId() == null) {
                continue;
            }
            ApplyMaterialReq r = new ApplyMaterialReq();
            r.setFileName(m.getFileName());
            r.setFileUrl(m.getFileUrl());
            byItem.computeIfAbsent(m.getApplyItemId(), k -> new ArrayList<>()).add(r);
        }
        for (MyApplyVO vo : list) {
            Long aid = vo.getApplyItemId();
            vo.setMaterials(aid == null ? List.of() : byItem.getOrDefault(aid, List.of()));
        }
        return list;
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
        return flattenOverviewToSections(buildCategoryScoreOverview(periodId, uid, true));
    }

    @Override
    public StudentCategoryScoreOverviewVO listMyCategoryScoreOverview(Long periodId) {
        if (periodId == null || periodId <= 0) {
            throw new IllegalArgumentException("请选择有效综测周期");
        }
        periodWorkflowService.requirePeriod(periodId);
        Long uid = SecurityContextUtil.getCurrentUserId().longValue();
        return buildCategoryScoreOverview(periodId, uid, true);
    }

    private StudentCategoryScoreOverviewVO buildCategoryScoreOverview(
            Long periodId, Long uid, boolean onlyStudentVisibleCategories) {
        List<StudentApplyApprovedScoreRow> rows = studentApplyMapper.listApprovedScoresForPeriod(uid, periodId);
        Rule rule = ruleMapper.findLatestByPeriodId(periodId.intValue());
        List<RuleCategory> ruleCats =
                (rule == null || rule.getId() == null) ? List.of() : ruleCategoryMapper.listByRuleId(rule.getId());
        Map<Integer, RuleCategory> byId = ruleCats.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(RuleCategory::getId, c -> c, (a, b) -> a));
        Predicate<Integer> vis = onlyStudentVisibleCategories
                ? id -> itemCategoryVisibleForStudent(id, byId)
                : id -> true;

        BigDecimal position = BigDecimal.ZERO;
        BigDecimal otherCustom = BigDecimal.ZERO;
        List<ComprehensiveScoreCalculator.RuleItemScoreRow> ruleRows = new ArrayList<>();
        for (StudentApplyApprovedScoreRow row : rows) {
            if (StringUtils.hasText(row.getSourceType()) && "CUSTOM".equalsIgnoreCase(row.getSourceType().trim())) {
                BigDecimal v = ApplyItemScoreUtil.effectiveScore(
                        row.getScore(),
                        row.getSourceType(),
                        row.getBaseScore(),
                        row.getCoeff(),
                        row.getScoreMode());
                if (ApplyScoreConstants.isPositionScoreCustomName(row.getCustomName())) {
                    position = position.add(v);
                } else if (!ApplyScoreConstants.isCategorySubmitNoneCustomName(row.getCustomName())
                        && !ApplyScoreConstants.isPositionSubmitNoneCustomName(row.getCustomName())) {
                    otherCustom = otherCustom.add(v);
                }
                continue;
            }
            ruleRows.add(new ComprehensiveScoreCalculator.RuleItemScoreRow(
                    row.getItemName(),
                    row.getModuleCode(),
                    row.getSubmoduleCode(),
                    row.getLevel(),
                    row.getBaseScore(),
                    row.getCoeff(),
                    row.getScoreMode(),
                    row.getDedupeGroup(),
                    row.getRuleItemId(),
                    row.getScore(),
                    row.getSourceType(),
                    row.getItemCategory(),
                    row.getApplyItemId()));
        }
        MyAcademicScoreVO ac = academicScoreService.getMyScore(periodId);
        BigDecimal intellectual = BigDecimal.ZERO;
        if (ac != null && ac.getIntellectualScore() != null) {
            intellectual = ac.getIntellectualScore();
        }
        return CategoryUnitScoreCalculator.buildOverview(
                intellectual,
                ruleRows,
                position,
                otherCustom,
                ruleCats,
                vis,
                ScorePolicySnapshot.defaults());
    }

    private static List<StudentSectionScoreVO> flattenOverviewToSections(StudentCategoryScoreOverviewVO overview) {
        if (overview == null) {
            return List.of();
        }
        List<StudentSectionScoreVO> out = new ArrayList<>();
        for (StudentCategoryScoreNodeVO root : overview.getCategoryRoots()) {
            appendCategoryNodeFlat(root, "", out);
        }
        int ei = 0;
        for (StudentScoreExtraRowVO ex : overview.getExtraRows()) {
            StudentSectionScoreVO vo = new StudentSectionScoreVO();
            vo.setSectionCode("extra-" + (ei++));
            vo.setSectionTitle(ex.getLabel());
            vo.setEarnedScore(ex.getScore());
            out.add(vo);
        }
        if (overview.getTotalScore() != null) {
            StudentSectionScoreVO sum = new StudentSectionScoreVO();
            sum.setSectionCode("grand-total");
            sum.setSectionTitle("合计（分类树 + 上述附加项）");
            sum.setEarnedScore(overview.getTotalScore());
            out.add(sum);
        }
        return out;
    }

    private static void appendCategoryNodeFlat(
            StudentCategoryScoreNodeVO node, String prefix, List<StudentSectionScoreVO> out) {
        String title = prefix + (node.getCategoryName() == null ? "-" : node.getCategoryName());
        StudentSectionScoreVO vo = new StudentSectionScoreVO();
        vo.setSectionCode("cat-" + (node.getCategoryId() == null ? "na" : node.getCategoryId()));
        vo.setSectionTitle(title);
        vo.setEarnedScore(node.getTotalScore());
        out.add(vo);
        String nextPrefix = prefix + "　";
        if (node.getChildren() != null) {
            for (StudentCategoryScoreNodeVO ch : node.getChildren()) {
                appendCategoryNodeFlat(ch, nextPrefix, out);
            }
        }
    }

    @Override
    public MaterialUploadVO uploadApplyMaterial(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请选择要上传的文件");
        }
        Integer uid = SecurityContextUtil.getCurrentUserId();
        String original = file.getOriginalFilename();
        if (!StringUtils.hasText(original)) {
            original = "file.bin";
        }
        String normalized = original.replace('\\', '/');
        int slash = normalized.lastIndexOf('/');
        String nameOnly = slash >= 0 ? normalized.substring(slash + 1) : normalized;
        String ext = "";
        int dot = nameOnly.lastIndexOf('.');
        if (dot >= 0 && dot < nameOnly.length() - 1) {
            ext = nameOnly.substring(dot + 1).toLowerCase();
        }
        if (!MATERIAL_ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("不支持的文件类型，允许：" + String.join("、", MATERIAL_ALLOWED_EXT));
        }
        String safeBase = nameOnly.replaceAll("[^a-zA-Z0-9._\\u4e00-\\u9fa5-]", "_");
        if (safeBase.length() > 150) {
            safeBase = safeBase.substring(safeBase.length() - 150);
        }
        String objectKey = "apply-materials/" + uid + "/" + UUID.randomUUID() + "_" + safeBase;
        minioUtil.upload(file, objectKey);
        MaterialUploadVO vo = new MaterialUploadVO();
        vo.setFileName(nameOnly);
        vo.setFileUrl(objectKey);
        vo.setContentType(file.getContentType());
        return vo;
    }

    @Override
    public String buildMaterialPreviewUrl(String objectKey) {
        if (!StringUtils.hasText(objectKey)) {
            throw new IllegalArgumentException("材料不存在");
        }
        Integer uid = SecurityContextUtil.getCurrentUserId();
        String key = objectKey.trim();
        assertMaterialKeyForUser(uid, key);
        if (!minioUtil.objectExists(key)) {
            throw new IllegalArgumentException("文件已失效或不存在");
        }
        return minioUtil.getPreviewUrl(key, 60, TimeUnit.MINUTES);
    }

    /** 仅细则项支持 quantity；任职分、非细则恒为 1 条。 */
    private static int expandQuantityForItem(ApplyItemReq itemReq) {
        if (itemReq == null) {
            return 1;
        }
        boolean isRuleItem = itemReq.getRuleItemId() != null && itemReq.getRuleItemId() > 0;
        if (!isRuleItem) {
            return 1;
        }
        return normalizeQuantity(itemReq.getQuantity());
    }

    private static int sumSubmitItemQuantities(List<ApplyItemReq> items) {
        if (items == null) {
            return 0;
        }
        int s = 0;
        for (ApplyItemReq r : items) {
            s += expandQuantityForItem(r);
        }
        return s;
    }

    private static int normalizeQuantity(Integer q) {
        if (q == null) {
            return 1;
        }
        if (q < 1) {
            return 1;
        }
        if (q > 99) {
            return 99;
        }
        return q;
    }

    private static BigDecimal normalizeScoreRatio(BigDecimal r) {
        if (r == null) {
            return BigDecimal.ONE;
        }
        BigDecimal x = r.setScale(4, RoundingMode.HALF_UP);
        BigDecimal min = new BigDecimal("0.01");
        if (x.compareTo(min) < 0) {
            return min;
        }
        if (x.compareTo(BigDecimal.ONE) > 0) {
            return BigDecimal.ONE;
        }
        return x;
    }

    private static void assertMaterialKeyForUser(Integer userId, String fileUrlOrKey) {
        if (userId == null || !StringUtils.hasText(fileUrlOrKey)) {
            throw new IllegalArgumentException("材料无效");
        }
        String t = fileUrlOrKey.trim();
        if (t.startsWith("http://") || t.startsWith("https://")) {
            return;
        }
        String prefix = "apply-materials/" + userId + "/";
        if (!t.startsWith(prefix)) {
            throw new IllegalArgumentException("材料链接无效，请使用本页上传生成的材料");
        }
    }

    private void validateAndInsertItem(Long applyId, ApplyItemReq itemReq, Integer studentUserId, Long periodId) {
        if (itemReq == null) {
            throw new IllegalArgumentException("申报项不能为空");
        }
        if (itemReq.getDeclaredScore() != null
                && !ApplyScoreConstants.isPositionScoreCustomName(
                        itemReq.getCustomName() == null ? "" : itemReq.getCustomName())) {
            throw new IllegalArgumentException("参数非法：仅任职分可自填分值");
        }
        boolean isRuleItem = itemReq.getRuleItemId() != null && itemReq.getRuleItemId() > 0;
        boolean isPositionScore =
                !isRuleItem && ApplyScoreConstants.isPositionScoreCustomName(itemReq.getCustomName());
        if (!isRuleItem && ApplyScoreConstants.isCategorySubmitNoneCustomName(itemReq.getCustomName())) {
            throw new IllegalArgumentException("请使用页面上的「本分类无奖项」入口提交");
        }
        if (!isRuleItem && ApplyScoreConstants.isPositionSubmitNoneCustomName(itemReq.getCustomName())) {
            throw new IllegalArgumentException("请使用页面上的「无任职分可报」入口提交");
        }
        if (!isRuleItem && !isPositionScore) {
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
        } else if (isPositionScore) {
            if (itemReq.getDeclaredScore() == null) {
                throw new IllegalArgumentException("任职分请填写分值");
            }
            BigDecimal dec = itemReq.getDeclaredScore().setScale(2, RoundingMode.HALF_UP);
            if (dec.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("任职分不能为负数");
            }
        } else {
            String moduleCode = studentApplyMapper.findModuleCodeByRuleItemId(itemReq.getRuleItemId());
            if ("ACADEMIC".equalsIgnoreCase(moduleCode)) {
                String adhocName = studentApplyMapper.findItemNameByRuleItemId(itemReq.getRuleItemId());
                if (!ApplyScoreConstants.isStudentAllowedAcademicAdhocRuleItemName(adhocName)) {
                    throw new IllegalArgumentException("学业水平（智育）由管理端维护，学生端不可申报该项");
                }
            }
            Integer needMaterial = studentApplyMapper.findNeedMaterialByRuleItemId(itemReq.getRuleItemId());
            if (needMaterial == null) {
                throw new IllegalArgumentException("存在无效细则项");
            }
            if (needMaterial == 1 && CollectionUtils.isEmpty(itemReq.getMaterials())) {
                throw new IllegalArgumentException("该细则项必须上传证明材料");
            }
            Rule rule = ruleMapper.findLatestByPeriodId(periodId.intValue());
            if (rule == null || rule.getId() == null) {
                throw new IllegalArgumentException("当前周期暂无适用规则");
            }
            Integer itemRuleId = studentApplyMapper.findRuleIdByRuleItemId(itemReq.getRuleItemId());
            if (!Objects.equals(itemRuleId, rule.getId())) {
                throw new IllegalArgumentException("细则项与当前周期规则不匹配");
            }
            List<RuleCategory> cats = ruleCategoryMapper.listByRuleId(rule.getId());
            Map<Integer, RuleCategory> byId = cats == null || cats.isEmpty()
                    ? Map.of()
                    : cats.stream()
                            .filter(c -> c.getId() != null)
                            .collect(Collectors.toMap(RuleCategory::getId, c -> c, (a, b) -> a));
            Integer ic = studentApplyMapper.findItemCategoryByRuleItemId(itemReq.getRuleItemId());
            if (!itemCategoryVisibleForStudent(ic, byId)) {
                throw new IllegalArgumentException("该细则项所属分类已关闭学生申报");
            }
        }

        EvaluationApplyItem item = new EvaluationApplyItem();
        item.setApplyId(applyId);
        item.setRuleItemId(isRuleItem ? itemReq.getRuleItemId() : null);
        if (isRuleItem) {
            RuleItemScoreMeta meta = studentApplyMapper.selectRuleItemScoreMeta(itemReq.getRuleItemId());
            if (meta == null) {
                throw new IllegalArgumentException("存在无效细则项");
            }
            BigDecimal ratio = normalizeScoreRatio(itemReq.getScoreRatio());
            BigDecimal declared = ApplyItemScoreUtil.declaredRuleItemScore(
                    meta.getBaseScore(),
                    meta.getCoeff(),
                    meta.getScoreMode(),
                    ratio);
            item.setScore(declared.setScale(2, RoundingMode.HALF_UP));
        } else {
            item.setScore(
                    isPositionScore
                            ? itemReq.getDeclaredScore().setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO);
        }
        item.setStatus("PENDING");
        item.setSourceType(isRuleItem ? "RULE" : "CUSTOM");
        item.setCustomName(isRuleItem ? null : itemReq.getCustomName().trim());
        String remarkTrim = StringUtils.hasText(itemReq.getRemark()) ? itemReq.getRemark().trim() : null;
        if (isRuleItem) {
            BigDecimal ratio = normalizeScoreRatio(itemReq.getScoreRatio());
            if (ratio.compareTo(BigDecimal.ONE) != 0) {
                String tag = "【申报分数比例:" + ratio.stripTrailingZeros().toPlainString() + "】";
                item.setRemark(remarkTrim == null ? tag : tag + remarkTrim);
            } else {
                item.setRemark(remarkTrim);
            }
        } else {
            item.setRemark(remarkTrim);
        }
        studentApplyMapper.insertApplyItem(item);

        if (!CollectionUtils.isEmpty(itemReq.getMaterials())) {
            for (ApplyMaterialReq materialReq : itemReq.getMaterials()) {
                if (materialReq == null) continue;
                if (!StringUtils.hasText(materialReq.getFileName()) || !StringUtils.hasText(materialReq.getFileUrl())) {
                    throw new IllegalArgumentException("材料文件名和链接不能为空");
                }
                assertMaterialKeyForUser(studentUserId, materialReq.getFileUrl());
                EvaluationApplyMaterial material = new EvaluationApplyMaterial();
                material.setApplyItemId(item.getId());
                material.setFileName(materialReq.getFileName().trim());
                material.setFileUrl(materialReq.getFileUrl().trim());
                studentApplyMapper.insertApplyMaterial(material);
            }
        }
    }
}
