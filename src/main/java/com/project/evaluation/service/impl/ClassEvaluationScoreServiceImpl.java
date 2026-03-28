package com.project.evaluation.service.impl;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.mapper.AcademicScoreMapper;
import com.project.evaluation.mapper.ClassEvaluationScoreMapper;
import com.project.evaluation.service.ClassEvaluationScoreService;
import com.project.evaluation.service.TeacherScopeService;
import com.project.evaluation.utils.ComprehensiveScoreCalculator;
import com.project.evaluation.vo.ClassScore.ApprovedRuleItemScoreRow;
import com.project.evaluation.vo.ClassScore.ClassEvaluationScoreRowVO;
import com.project.evaluation.vo.ClassScore.ClassEvaluationStudentRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClassEvaluationScoreServiceImpl implements ClassEvaluationScoreService {

    @Autowired
    private ClassEvaluationScoreMapper classEvaluationScoreMapper;

    @Autowired
    private AcademicScoreMapper academicScoreMapper;

    @Autowired
    private TeacherScopeService teacherScopeService;

    @Override
    public PageBean<ClassEvaluationScoreRowVO> page(Integer pageNum, Integer pageSize,
                                                    Long periodId, Long classId, String studentNo) {
        if (periodId == null || periodId <= 0) {
            throw new IllegalArgumentException("请选择综测周期");
        }
        if (academicScoreMapper.countPeriod(periodId) == 0) {
            throw new IllegalArgumentException("综测周期不存在");
        }
        Scope scope = resolveScope(classId);
        if (scope.emptyResult()) {
            return new PageBean<>(0L, Collections.emptyList());
        }

        List<ClassEvaluationStudentRow> studs;
        PageInfo<ClassEvaluationStudentRow> pageInfo;
        try (Page<Object> ignored = PageHelper.startPage(pageNum, pageSize)) {
            studs = classEvaluationScoreMapper.listStudentsForScore(
                    periodId, scope.classIdsFilter(), scope.singleClassId(), studentNo);
            pageInfo = new PageInfo<>(studs);
        }

        List<Long> userIds = studs.stream().map(ClassEvaluationStudentRow::getUserId).toList();
        Map<Long, List<ApprovedRuleItemScoreRow>> itemMap = loadItemsGrouped(periodId, userIds, scope);

        List<ClassEvaluationScoreRowVO> vos = buildVos(studs, itemMap);
        return new PageBean<>(pageInfo.getTotal(), vos);
    }

    @Override
    public byte[] exportExcel(Long periodId, Long classId, String studentNo) {
        if (periodId == null || periodId <= 0) {
            throw new IllegalArgumentException("请选择综测周期");
        }
        if (academicScoreMapper.countPeriod(periodId) == 0) {
            throw new IllegalArgumentException("综测周期不存在");
        }
        Scope scope = resolveScope(classId);
        if (scope.emptyResult()) {
            return toExcelBytes(Collections.emptyList());
        }

        List<ClassEvaluationStudentRow> studs = classEvaluationScoreMapper.listStudentsForScore(
                periodId, scope.classIdsFilter(), scope.singleClassId(), studentNo);
        List<Long> userIds = studs.stream().map(ClassEvaluationStudentRow::getUserId).toList();
        Map<Long, List<ApprovedRuleItemScoreRow>> itemMap = loadItemsGrouped(periodId, userIds, scope);
        List<ClassEvaluationScoreRowVO> vos = buildVos(studs, itemMap);
        return toExcelBytes(vos);
    }

    private static byte[] toExcelBytes(List<ClassEvaluationScoreRowVO> vos) {
        try (ExcelWriter writer = ExcelUtil.getWriter(true);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            writer.writeRow(List.of(
                    "学号", "姓名", "班级", "智育", "德育", "学业(智育)", "身心素养", "审美人文", "劳动素养", "创新素养", "总分(预估)"
            ));
            for (ClassEvaluationScoreRowVO r : vos) {
                // 不可使用 List.of：任一 null 会 NPE；库中姓名/班级等可能为空
                writer.writeRow(Arrays.asList(
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
                                                     Map<Long, List<ApprovedRuleItemScoreRow>> itemMap) {
        List<ClassEvaluationScoreRowVO> list = new ArrayList<>();
        for (ClassEvaluationStudentRow s : studs) {
            List<ApprovedRuleItemScoreRow> raw = itemMap.getOrDefault(s.getUserId(), List.of());
            List<ComprehensiveScoreCalculator.RuleItemScoreRow> calcRows = new ArrayList<>();
            for (ApprovedRuleItemScoreRow it : raw) {
                calcRows.add(new ComprehensiveScoreCalculator.RuleItemScoreRow(
                        it.getItemName(),
                        it.getModuleCode(),
                        it.getLevel(),
                        it.getBaseScore(),
                        it.getCoeff(),
                        it.getScoreMode()
                ));
            }
            BigDecimal intellectual = s.getIntellectualScore() != null ? s.getIntellectualScore() : BigDecimal.ZERO;
            Map<ComprehensiveScoreCalculator.Section, BigDecimal> sec = ComprehensiveScoreCalculator.sectionScores(
                    intellectual, calcRows);

            ClassEvaluationScoreRowVO vo = new ClassEvaluationScoreRowVO();
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
            vo.setTotalScore(ComprehensiveScoreCalculator.totalScore(sec));
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
