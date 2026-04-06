package com.project.evaluation.service.impl;

import com.project.evaluation.entity.EvaluationSubmitTip;
import com.project.evaluation.entity.Rule;
import com.project.evaluation.entity.RuleCategory;
import com.project.evaluation.mapper.EvaluationSubmitTipMapper;
import com.project.evaluation.mapper.RuleCategoryMapper;
import com.project.evaluation.mapper.RuleMapper;
import com.project.evaluation.service.EvaluationSubmitTipService;
import com.project.evaluation.service.PeriodEventLogService;
import com.project.evaluation.service.PeriodWorkflowService;
import com.project.evaluation.utils.SecurityContextUtil;
import com.project.evaluation.vo.SubmitTip.SubmitTipSaveReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class EvaluationSubmitTipServiceImpl implements EvaluationSubmitTipService {

    private static final Set<String> SECTION_CODES = Set.of(
            "moral", "academic", "quality_bodymind", "quality_art", "quality_labor", "quality_innovation"
    );

    @Autowired
    private EvaluationSubmitTipMapper evaluationSubmitTipMapper;

    @Autowired
    private RuleMapper ruleMapper;

    @Autowired
    private RuleCategoryMapper ruleCategoryMapper;

    @Autowired
    private PeriodWorkflowService periodWorkflowService;

    @Autowired
    private PeriodEventLogService periodEventLogService;

    @Override
    public List<RuleCategory> listCategoriesForTipManage(Long periodId) {
        if (periodId == null || periodId <= 0) {
            throw new IllegalArgumentException("请选择综测周期");
        }
        periodWorkflowService.requirePeriod(periodId);
        Rule rule = ruleMapper.findLatestByPeriodId(periodId.intValue());
        if (rule == null || rule.getId() == null) {
            return List.of();
        }
        List<RuleCategory> cats = ruleCategoryMapper.listByRuleId(rule.getId());
        return cats == null ? List.of() : cats;
    }

    @Override
    public List<EvaluationSubmitTip> listForManage(List<Long> periodIds, List<String> sectionCodes) {
        List<Long> pids = periodIds == null ? Collections.emptyList() : periodIds;
        if (!pids.isEmpty()) {
            for (Long pid : pids) {
                periodWorkflowService.requirePeriod(pid);
            }
        }
        List<String> scs = sectionCodes == null ? Collections.emptyList() : sectionCodes;
        List<String> normalized = scs.stream()
                .map(this::normalizeSectionCodeForFilter)
                .filter(s -> s != null && !s.isEmpty())
                .toList();
        return evaluationSubmitTipMapper.listForManage(pids, normalized.isEmpty() ? null : normalized);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(SubmitTipSaveReq req) {
        validateReq(req, false);
        periodWorkflowService.assertNotArchivedOnly(req.getPeriodId());
        EvaluationSubmitTip row = new EvaluationSubmitTip();
        row.setPeriodId(req.getPeriodId());
        row.setSectionCode(resolveSectionCodeForPeriod(req.getPeriodId(), req.getSectionCode(), true));
        row.setTitle(req.getTitle().trim());
        row.setContent(req.getContent().trim());
        row.setSortOrder(req.getSortOrder() == null ? 0 : req.getSortOrder());
        row.setStatus(req.getStatus() == null ? 1 : req.getStatus());
        row.setOperatorUserId(SecurityContextUtil.getCurrentUserId());
        evaluationSubmitTipMapper.insert(row);
        periodEventLogService.log(req.getPeriodId(), "SUBMIT_TIP_ADD", "新增申报提示 id=" + row.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SubmitTipSaveReq req) {
        validateReq(req, true);
        EvaluationSubmitTip old = evaluationSubmitTipMapper.selectById(req.getId());
        if (old == null) {
            throw new IllegalArgumentException("提示记录不存在");
        }
        periodWorkflowService.assertNotArchivedOnly(old.getPeriodId());
        String sectionCode = resolveSectionCodeForPeriod(req.getPeriodId(), req.getSectionCode(), true);
        int n = evaluationSubmitTipMapper.updateById(
                req.getId(), req.getPeriodId(), sectionCode, req.getTitle().trim(), req.getContent().trim(),
                req.getSortOrder() == null ? 0 : req.getSortOrder(), req.getStatus() == null ? 1 : req.getStatus(),
                SecurityContextUtil.getCurrentUserId()
        );
        if (n == 0) throw new IllegalStateException("更新失败");
        periodEventLogService.log(req.getPeriodId(), "SUBMIT_TIP_UPDATE", "更新申报提示 id=" + req.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("非法提示ID");
        EvaluationSubmitTip old = evaluationSubmitTipMapper.selectById(id);
        if (old == null) throw new IllegalArgumentException("提示记录不存在");
        periodWorkflowService.assertNotArchivedOnly(old.getPeriodId());
        if (evaluationSubmitTipMapper.deleteById(id) == 0) throw new IllegalStateException("删除失败");
        periodEventLogService.log(old.getPeriodId(), "SUBMIT_TIP_DELETE", "删除申报提示 id=" + id);
    }

    @Override
    public List<EvaluationSubmitTip> listForStudent(Long periodId, String sectionCode) {
        periodWorkflowService.requirePeriod(periodId);
        String normalized = resolveSectionCodeForStudent(periodId, sectionCode);
        return evaluationSubmitTipMapper.listForStudent(periodId, normalized);
    }

    private static void validateReq(SubmitTipSaveReq req, boolean requireId) {
        if (req == null) throw new IllegalArgumentException("请求不能为空");
        if (requireId && (req.getId() == null || req.getId() <= 0)) throw new IllegalArgumentException("非法提示ID");
        if (req.getPeriodId() == null || req.getPeriodId() <= 0) throw new IllegalArgumentException("请选择综测周期");
        if (!StringUtils.hasText(req.getSectionCode())) throw new IllegalArgumentException("请选择分类");
        if (!StringUtils.hasText(req.getTitle())) throw new IllegalArgumentException("请输入提示标题");
        if (!StringUtils.hasText(req.getContent())) throw new IllegalArgumentException("请输入提示内容");
        if (req.getTitle().trim().length() > 200) throw new IllegalArgumentException("提示标题请勿超过200字");
        if (req.getContent().trim().length() > 4000) throw new IllegalArgumentException("提示内容请勿超过4000字");
        if (req.getStatus() != null && req.getStatus() != 0 && req.getStatus() != 1) {
            throw new IllegalArgumentException("状态仅支持 0 或 1");
        }
    }

    private String normalizeSectionCodeForFilter(String sectionCode) {
        if (!StringUtils.hasText(sectionCode)) {
            return null;
        }
        String raw = sectionCode.trim();
        String lower = raw.toLowerCase();
        if (SECTION_CODES.contains(lower)) {
            return lower;
        }
        if (raw.matches("^\\d+$")) {
            return String.valueOf(Integer.parseInt(raw));
        }
        return null;
    }

    /**
     * 学生端：未传或空表示全部；否则仅支持历史分区编码或当前周期规则下的分类 id（数字字符串）。
     */
    private String resolveSectionCodeForStudent(Long periodId, String sectionCode) {
        if (!StringUtils.hasText(sectionCode)) {
            return null;
        }
        String raw = sectionCode.trim();
        String lower = raw.toLowerCase();
        if (SECTION_CODES.contains(lower)) {
            return lower;
        }
        if (raw.matches("^\\d+$")) {
            int cid = Integer.parseInt(raw);
            assertCategoryBelongsToPeriod(periodId, cid);
            return String.valueOf(cid);
        }
        throw new IllegalArgumentException("未知分类编码: " + sectionCode);
    }

    private String resolveSectionCodeForPeriod(Long periodId, String sectionCode, boolean required) {
        if (!StringUtils.hasText(sectionCode)) {
            if (required) {
                throw new IllegalArgumentException("请选择分类");
            }
            return null;
        }
        String raw = sectionCode.trim();
        String lower = raw.toLowerCase();
        if (SECTION_CODES.contains(lower)) {
            return lower;
        }
        if (raw.matches("^\\d+$")) {
            int cid = Integer.parseInt(raw);
            assertCategoryBelongsToPeriod(periodId, cid);
            return String.valueOf(cid);
        }
        if (required) {
            throw new IllegalArgumentException("未知分类编码: " + sectionCode);
        }
        return null;
    }

    private void assertCategoryBelongsToPeriod(Long periodId, int categoryId) {
        Rule rule = ruleMapper.findLatestByPeriodId(periodId.intValue());
        if (rule == null || rule.getId() == null) {
            throw new IllegalArgumentException("该周期尚未配置规则，无法使用规则分类");
        }
        RuleCategory cat = ruleCategoryMapper.findRuleCategoryById(categoryId);
        if (cat == null || cat.getRuleId() == null || !cat.getRuleId().equals(rule.getId())) {
            throw new IllegalArgumentException("分类不属于当前周期的规则");
        }
    }
}
