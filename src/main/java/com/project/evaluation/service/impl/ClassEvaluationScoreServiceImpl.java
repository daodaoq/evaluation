package com.project.evaluation.service.impl;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Time;
import com.project.evaluation.constant.ApplyScoreConstants;
import com.project.evaluation.entity.Rule;
import com.project.evaluation.entity.RuleCategory;
import com.project.evaluation.mapper.AcademicScoreMapper;
import com.project.evaluation.mapper.ClassEvaluationScoreMapper;
import com.project.evaluation.mapper.RuleCategoryMapper;
import com.project.evaluation.mapper.RuleMapper;
import com.project.evaluation.mapper.TimeMapper;
import com.project.evaluation.scorepolicy.ScorePolicySnapshot;
import com.project.evaluation.service.ClassEvaluationScoreService;
import com.project.evaluation.service.TeacherScopeService;
import com.project.evaluation.utils.ApplyItemScoreUtil;
import com.project.evaluation.utils.CategoryUnitScoreCalculator;
import com.project.evaluation.utils.ComprehensiveScoreCalculator;
import com.project.evaluation.vo.ClassScore.ApprovedRuleItemScoreRow;
import com.project.evaluation.vo.ClassScore.ClassEvaluationScoreRowVO;
import com.project.evaluation.vo.ClassScore.ClassEvaluationStudentRow;
import com.project.evaluation.vo.ClassScore.ClassUnsubmittedRowVO;
import com.project.evaluation.vo.StudentApply.StudentCategoryScoreOverviewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ClassEvaluationScoreServiceImpl implements ClassEvaluationScoreService {

    @Autowired
    private ClassEvaluationScoreMapper classEvaluationScoreMapper;

    @Autowired
    private AcademicScoreMapper academicScoreMapper;

    @Autowired
    private TeacherScopeService teacherScopeService;

    @Autowired
    private TimeMapper timeMapper;

    @Autowired
    private RuleMapper ruleMapper;

    @Autowired
    private RuleCategoryMapper ruleCategoryMapper;

    @Override
    public PageBean<ClassEvaluationScoreRowVO> page(Integer pageNum, Integer pageSize,
                                                    List<Long> periodIds, Long classId, String studentNo, String totalSortOrder) {
        List<Long> pids = resolvePeriodIds(periodIds);
        Scope scope = resolveScope(classId);
        if (scope.emptyResult()) {
            return new PageBean<>(0L, Collections.emptyList());
        }
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        List<ClassEvaluationScoreRowVO> vos = listScoreRowsAllPeriods(pids, scope, studentNo);
        applyTotalSort(vos, totalSortOrder);
        long total = vos.size();
        int from = (safePageNum - 1) * safePageSize;
        if (from >= vos.size()) {
            return new PageBean<>(total, Collections.emptyList());
        }
        int to = Math.min(from + safePageSize, vos.size());
        return new PageBean<>(total, vos.subList(from, to));
    }

    @Override
    public byte[] exportExcel(List<Long> periodIds, Long classId, String studentNo, String totalSortOrder) {
        List<Long> pids = resolvePeriodIds(periodIds);
        Scope scope = resolveScope(classId);
        if (scope.emptyResult()) {
            return toExcelBytes(Collections.emptyList());
        }
        List<ClassEvaluationScoreRowVO> vos = listScoreRowsAllPeriods(pids, scope, studentNo);
        applyTotalSort(vos, totalSortOrder);
        return toExcelBytes(vos);
    }

    @Override
    public PageBean<ClassUnsubmittedRowVO> pageUnsubmitted(Integer pageNum, Integer pageSize,
                                                           List<Long> periodIds, Long classId, String studentNo) {
        List<Long> pids = resolvePeriodIds(periodIds);
        Scope scope = resolveScope(classId);
        if (scope.emptyResult()) return new PageBean<>(0L, Collections.emptyList());
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        List<ClassUnsubmittedRowVO> rows = listUnsubmittedAllPeriods(pids, scope, studentNo);
        long total = rows.size();
        int from = (safePageNum - 1) * safePageSize;
        if (from >= rows.size()) return new PageBean<>(total, Collections.emptyList());
        int to = Math.min(from + safePageSize, rows.size());
        return new PageBean<>(total, rows.subList(from, to));
    }

    @Override
    public byte[] exportUnsubmittedExcel(List<Long> periodIds, Long classId, String studentNo) {
        List<Long> pids = resolvePeriodIds(periodIds);
        Scope scope = resolveScope(classId);
        if (scope.emptyResult()) return toUnsubmittedExcelBytes(Collections.emptyList());
        List<ClassUnsubmittedRowVO> rows = listUnsubmittedAllPeriods(pids, scope, studentNo);
        return toUnsubmittedExcelBytes(rows);
    }

    private List<Long> resolvePeriodIds(List<Long> requested) {
        if (requested != null && !requested.isEmpty()) {
            LinkedHashSet<Long> set = new LinkedHashSet<>();
            for (Long id : requested) {
                if (id != null && id > 0 && academicScoreMapper.countPeriod(id) > 0) {
                    set.add(id);
                }
            }
            return new ArrayList<>(set);
        }
        List<Time> all = timeMapper.timeList();
        if (all == null || all.isEmpty()) {
            return Collections.emptyList();
        }
        return all.stream()
                .map(Time::getId)
                .filter(Objects::nonNull)
                .map(Integer::longValue)
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    private List<ClassEvaluationScoreRowVO> listScoreRowsAllPeriods(List<Long> pids, Scope scope, String studentNo) {
        List<ClassEvaluationScoreRowVO> combined = new ArrayList<>();
        for (Long periodId : pids) {
            List<ClassEvaluationStudentRow> studs = classEvaluationScoreMapper.listStudentsForScore(
                    periodId, scope.classIdsFilter(), scope.singleClassId(), studentNo);
            List<Long> userIds = studs.stream().map(ClassEvaluationStudentRow::getUserId).toList();
            Map<Long, List<ApprovedRuleItemScoreRow>> itemMap = loadItemsGrouped(periodId, userIds, scope);
            combined.addAll(buildVos(studs, itemMap, periodId));
        }
        return combined;
    }

    private List<ClassUnsubmittedRowVO> listUnsubmittedAllPeriods(List<Long> pids, Scope scope, String studentNo) {
        List<ClassUnsubmittedRowVO> combined = new ArrayList<>();
        for (Long periodId : pids) {
            List<ClassUnsubmittedRowVO> part = classEvaluationScoreMapper.listUnsubmittedStudents(
                    periodId, scope.classIdsFilter(), scope.singleClassId(), studentNo);
            for (ClassUnsubmittedRowVO r : part) {
                r.setPeriodId(periodId);
            }
            combined.addAll(part);
        }
        return combined;
    }

    private static void applyTotalSort(List<ClassEvaluationScoreRowVO> vos, String totalSortOrder) {
        if (vos == null || vos.size() <= 1 || totalSortOrder == null) {
            return;
        }
        String order = totalSortOrder.trim().toLowerCase();
        Comparator<ClassEvaluationScoreRowVO> cmp = Comparator.comparing(
                v -> v.getTotalScore() == null ? BigDecimal.ZERO : v.getTotalScore()
        );
        if ("asc".equals(order)) {
            vos.sort(cmp.thenComparing(v -> blankIfNull(v.getStudentNo())));
        } else if ("desc".equals(order)) {
            vos.sort(cmp.reversed().thenComparing(v -> blankIfNull(v.getStudentNo())));
        }
    }

    private static byte[] toExcelBytes(List<ClassEvaluationScoreRowVO> vos) {
        try (ExcelWriter writer = ExcelUtil.getWriter(true);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            writer.writeRow(List.of(
                    "周期ID", "学号", "姓名", "班级", "智育", "德育", "学业(智育)", "身心素养", "审美人文", "劳动素养", "创新素养", "总分(预估)"
            ));
            for (ClassEvaluationScoreRowVO r : vos) {
                // 不可使用 List.of：任一 null 会 NPE；库中姓名/班级等可能为空
                writer.writeRow(Arrays.asList(
                        r.getPeriodId() == null ? "" : String.valueOf(r.getPeriodId()),
                        blankIfNull(r.getStudentNo()),
                        blankIfNull(r.getStudentName()),
                        blankIfNull(r.getClassName()),
                        nz(r.getIntellectualScore()),
                        nz(r.getMoralScore()),
                        nz(r.getAcademicScore()),
                        nz(r.getQualityBodymindScore()),
                        nz(r.getQualityArtScore()),
                        nz(r.getQualityLaborScore()),
                        nz(r.getQualityInnovationScore()),
                        nz(r.getTotalScore())
                ));
            }
            writer.flush(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("导出 Excel 失败", e);
        }
    }

    private static byte[] toUnsubmittedExcelBytes(List<ClassUnsubmittedRowVO> rows) {
        try (ExcelWriter writer = ExcelUtil.getWriter(true);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            writer.writeRow(List.of("周期ID", "学号", "姓名", "班级"));
            for (ClassUnsubmittedRowVO r : rows) {
                writer.writeRow(Arrays.asList(
                        r.getPeriodId() == null ? "" : String.valueOf(r.getPeriodId()),
                        blankIfNull(r.getStudentNo()),
                        blankIfNull(r.getStudentName()),
                        blankIfNull(r.getClassName())
                ));
            }
            writer.flush(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("导出未提交名单失败", e);
        }
    }

    private static String blankIfNull(String s) {
        return s == null ? "" : s;
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private Map<Long, List<ApprovedRuleItemScoreRow>> loadItemsGrouped(Long periodId, List<Long> userIds, Scope scope) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ApprovedRuleItemScoreRow> items = classEvaluationScoreMapper.listApprovedRuleItemsForPeriod(
                periodId, userIds, scope.classIdsFilter(), scope.singleClassId());
        return items.stream().collect(Collectors.groupingBy(ApprovedRuleItemScoreRow::getStudentUserId));
    }

    private List<ClassEvaluationScoreRowVO> buildVos(List<ClassEvaluationStudentRow> studs,
                                                     Map<Long, List<ApprovedRuleItemScoreRow>> itemMap,
                                                     Long periodId) {
        ScorePolicySnapshot policy = ScorePolicySnapshot.defaults();
        Rule rule = ruleMapper.findLatestByPeriodId(periodId.intValue());
        List<RuleCategory> ruleCats =
                (rule == null || rule.getId() == null) ? List.of() : ruleCategoryMapper.listByRuleId(rule.getId());
        List<ClassEvaluationScoreRowVO> list = new ArrayList<>();
        for (ClassEvaluationStudentRow s : studs) {
            List<ApprovedRuleItemScoreRow> raw = itemMap.getOrDefault(s.getUserId(), List.of());
            BigDecimal position = BigDecimal.ZERO;
            BigDecimal otherCustom = BigDecimal.ZERO;
            List<ComprehensiveScoreCalculator.RuleItemScoreRow> rulesOnly = new ArrayList<>();
            List<ComprehensiveScoreCalculator.RuleItemScoreRow> allForSection = new ArrayList<>();
            for (ApprovedRuleItemScoreRow it : raw) {
                ComprehensiveScoreCalculator.RuleItemScoreRow row = new ComprehensiveScoreCalculator.RuleItemScoreRow(
                        it.getItemName(),
                        it.getModuleCode(),
                        it.getSubmoduleCode(),
                        it.getLevel(),
                        it.getBaseScore(),
                        it.getCoeff(),
                        it.getScoreMode(),
                        it.getDedupeGroup(),
                        it.getRuleItemId(),
                        it.getPersistedScore(),
                        it.getSourceType(),
                        it.getItemCategory(),
                        it.getApplyItemId());
                if (it.getSourceType() != null && "CUSTOM".equalsIgnoreCase(it.getSourceType().trim())) {
                    BigDecimal v = ApplyItemScoreUtil.effectiveScore(
                            it.getPersistedScore(),
                            it.getSourceType(),
                            it.getBaseScore(),
                            it.getCoeff(),
                            it.getScoreMode());
                    if (ApplyScoreConstants.isPositionScoreCustomName(it.getItemName())) {
                        position = position.add(v);
                    } else if (!ApplyScoreConstants.isCategorySubmitNoneCustomName(it.getItemName())
                            && !ApplyScoreConstants.isPositionSubmitNoneCustomName(it.getItemName())) {
                        otherCustom = otherCustom.add(v);
                    }
                    allForSection.add(row);
                    continue;
                }
                rulesOnly.add(row);
                allForSection.add(row);
            }
            BigDecimal intellectual = s.getIntellectualScore() != null ? s.getIntellectualScore() : BigDecimal.ZERO;
            StudentCategoryScoreOverviewVO cov = CategoryUnitScoreCalculator.buildOverview(
                    intellectual,
                    rulesOnly,
                    position,
                    otherCustom,
                    ruleCats,
                    id -> true,
                    policy);
            Map<ComprehensiveScoreCalculator.Section, BigDecimal> sec =
                    ComprehensiveScoreCalculator.sectionScores(intellectual, allForSection, policy);

            ClassEvaluationScoreRowVO vo = new ClassEvaluationScoreRowVO();
            vo.setPeriodId(periodId);
            vo.setUserId(s.getUserId());
            vo.setStudentNo(s.getStudentNo());
            vo.setStudentName(s.getStudentName());
            vo.setClassId(s.getClassId());
            vo.setClassName(s.getClassName());
            vo.setIntellectualScore(intellectual);
            vo.setMoralScore(scale(sec.get(ComprehensiveScoreCalculator.Section.MORAL)));
            vo.setAcademicScore(scale(sec.get(ComprehensiveScoreCalculator.Section.ACADEMIC)));
            vo.setQualityBodymindScore(scale(sec.get(ComprehensiveScoreCalculator.Section.QUALITY_BODYMIND)));
            vo.setQualityArtScore(scale(sec.get(ComprehensiveScoreCalculator.Section.QUALITY_ART)));
            vo.setQualityLaborScore(scale(sec.get(ComprehensiveScoreCalculator.Section.QUALITY_LABOR)));
            vo.setQualityInnovationScore(scale(sec.get(ComprehensiveScoreCalculator.Section.QUALITY_INNOVATION)));
            vo.setTotalScore(scale(cov.getTotalScore()));
            list.add(vo);
        }
        return list;
    }

    private static BigDecimal scale(BigDecimal v) {
        if (v == null) {
            return BigDecimal.ZERO;
        }
        return v.setScale(8, java.math.RoundingMode.HALF_UP);
    }

    private Scope resolveScope(Long classId) {
        TeacherScopeService.StudentMenuScope scope = teacherScopeService.resolveStudentMenuScope();
        if (scope == TeacherScopeService.StudentMenuScope.DENIED) {
            throw new AccessDeniedException("无权限");
        }
        if (scope == TeacherScopeService.StudentMenuScope.ADMIN) {
            return new Scope(null, classId, false);
        }
        List<Integer> managed = teacherScopeService.getManagedClassIdsForCurrentTeacher();
        if (managed == null || managed.isEmpty()) {
            return new Scope(null, null, true);
        }
        if (classId != null) {
            if (!managed.contains(classId.intValue())) {
                throw new IllegalArgumentException("您无权查看该班级数据");
            }
        }
        return new Scope(managed, classId, false);
    }

    private record Scope(List<Integer> classIdsFilter, Long singleClassId, boolean emptyResult) {}
}
