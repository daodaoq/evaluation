package com.project.evaluation.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Rule;
import com.project.evaluation.entity.RuleCategory;
import com.project.evaluation.mapper.RuleCategoryMapper;
import com.project.evaluation.mapper.RuleMapper;
import com.project.evaluation.service.RuleCategoryService;
import com.project.evaluation.vo.RuleCategory.AddRuleCategoryReq;
import com.project.evaluation.vo.RuleCategory.RuleCategoryCopyPreviewVO;
import com.project.evaluation.vo.RuleCategory.UpdateRuleCategoryReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class RuleCategoryServiceImpl implements RuleCategoryService {

    @Autowired
    private RuleCategoryMapper ruleCategoryMapper;

    @Autowired
    private RuleMapper ruleMapper;

    /**
     * 添加规则分类
     * @param addRuleCategoryReq
     */
    @Override
    public void addRuleCategory(AddRuleCategoryReq addRuleCategoryReq) {
        if (addRuleCategoryReq.getRuleId() == null || addRuleCategoryReq.getRuleId() <= 0) {
            throw new IllegalArgumentException("请选择有效的规则总览（综测周期下的规则）");
        }
        assertParentCategoryBelongsToSameRule(addRuleCategoryReq.getRuleId(), addRuleCategoryReq.getParentId());
        if (addRuleCategoryReq.getStudentVisible() == null) {
            addRuleCategoryReq.setStudentVisible(1);
        }
        if (addRuleCategoryReq.getSortOrder() == null) {
            addRuleCategoryReq.setSortOrder(0);
        }
        if (addRuleCategoryReq.getCategoryBaseScore() == null) {
            addRuleCategoryReq.setCategoryBaseScore(java.math.BigDecimal.ZERO);
        }
        ruleCategoryMapper.addRuleCategory(addRuleCategoryReq);
        log.info("添加成功：{}",addRuleCategoryReq);
    }

    /**
     * 删除规则分类
     * @param id
     */
    @Override
    public void deleteRuleCategory(Integer id)  {
        if(id == null || id <= 0){
            throw new IllegalArgumentException("非法规则分类ID");
        }
        int rows = ruleCategoryMapper.deleteRuleCategory(id);
        if(rows == 0)
        {
            log.warn("删除失败，规则分类id不存在：{}",id);
            throw new IllegalStateException("规则分类不存在或已删除");
        }
        log.info("删除规则分类成功： id={}",id);

    }

    /**
     * 更新规则分类信息
     * @param updateRuleCategoryReq
     */
    @Override
    public void updateRuleCategory(Integer id, UpdateRuleCategoryReq updateRuleCategoryReq) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("非法规则分类ID");
        }
        RuleCategory existing = ruleCategoryMapper.findRuleCategoryById(id);
        if (existing == null) {
            throw new IllegalStateException("规则分类不存在");
        }
        Integer ruleId = existing.getRuleId();
        if (updateRuleCategoryReq.getRuleId() != null && !Objects.equals(updateRuleCategoryReq.getRuleId(), ruleId)) {
            throw new IllegalArgumentException("不得将分类改挂到其他综测周期的规则下，请保持规则总览 id 不变");
        }
        Integer parentId =
                updateRuleCategoryReq.getParentId() != null
                        ? updateRuleCategoryReq.getParentId()
                        : existing.getParentId();
        assertParentCategoryBelongsToSameRule(ruleId, parentId);
        int p = normParentId(parentId);
        if (p > 0 && p == id.intValue()) {
            throw new IllegalArgumentException("父级分类不能为自身");
        }
        ruleCategoryMapper.updateRuleCategory(id, updateRuleCategoryReq);
    }

    /**
     * 父级必须与本分类同属一个规则总览（rule_id），即同一综测周期下生效的那套规则，禁止跨周期/跨规则挂接。
     */
    private void assertParentCategoryBelongsToSameRule(Integer ruleId, Integer parentId) {
        if (ruleId == null || ruleId <= 0) {
            throw new IllegalArgumentException("规则总览 id 无效");
        }
        int p = normParentId(parentId);
        if (p == 0) {
            return;
        }
        RuleCategory parent = ruleCategoryMapper.findRuleCategoryById(p);
        if (parent == null) {
            throw new IllegalArgumentException("父级分类不存在");
        }
        if (!Objects.equals(parent.getRuleId(), ruleId)) {
            throw new IllegalArgumentException("父级分类必须与本分类属于同一综测周期下的规则（同一规则总览）");
        }
    }

    private static int normParentId(Integer parentId) {
        if (parentId == null || parentId <= 0) {
            return 0;
        }
        return parentId;
    }

    /**
     * 通过周其名称查找规则分类
     * @param name
     * @return RuleCategory
     */
    @Override
    public RuleCategory findRuleCategoryByName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("规则分类名称不能为空");
        }
        return ruleCategoryMapper.findRuleCategoryByName(name.trim());
    }

    /**
     * 通过 id 查找规则分类
     * @param id
     * @return
     */
    @Override
    public RuleCategory findRuleCategoryById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("非法规则分类ID");
        }
        return ruleCategoryMapper.findRuleCategoryById(id);
    }

    /**
     * 批量获取规则分类
     * @return
     */
    @Override
    public List<RuleCategory> ruleCategoryList(List<Integer> ruleIds) {
        List<Integer> ids = normalizeRuleIds(ruleIds);
        return ruleCategoryMapper.ruleCategoryList(ids);
    }

    /**
     * 分页条件查询规则分类
     * @param pageNum
     * @param pageSize
     * @param ruleIds 规则总览 id 列表，null 或空表示不限
     * @return
     */
    @Override
    public PageBean<RuleCategory> paginationQuery(Integer pageNum, Integer pageSize, List<Integer> ruleIds) {
        PageBean<RuleCategory> pb = new PageBean<>();

        PageHelper.startPage(pageNum, pageSize);

        List<Integer> ids = normalizeRuleIds(ruleIds);
        List<RuleCategory> ruleCategorys = ruleCategoryMapper.paginationQuery(ids);

        PageInfo<RuleCategory> info = new PageInfo<>(ruleCategorys);
        pb.setTotal(info.getTotal());
        pb.setItems(info.getList());
        return pb;
    }

    /** null 或全为非法 id 时返回 null，表示 Mapper 侧不加 rule_id 条件 */
    private static List<Integer> normalizeRuleIds(List<Integer> ruleIds) {
        if (ruleIds == null || ruleIds.isEmpty()) {
            return null;
        }
        List<Integer> cleaned =
                ruleIds.stream().filter(id -> id != null && id > 0).distinct().toList();
        return cleaned.isEmpty() ? null : cleaned;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int copyByPeriod(Integer sourcePeriodId, Integer targetPeriodId, Boolean overwrite) {
        if (sourcePeriodId == null || sourcePeriodId <= 0 || targetPeriodId == null || targetPeriodId <= 0) {
            throw new IllegalArgumentException("请选择有效学期");
        }
        if (sourcePeriodId.equals(targetPeriodId)) {
            throw new IllegalArgumentException("来源学期与目标学期不能相同");
        }
        Rule sourceRule = ruleMapper.findLatestByPeriodId(sourcePeriodId);
        if (sourceRule == null || sourceRule.getId() == null) {
            throw new IllegalStateException("来源学期暂无规则可复制");
        }
        Rule targetRule = ruleMapper.findLatestByPeriodId(targetPeriodId);
        if (targetRule == null || targetRule.getId() == null) {
            targetRule = new Rule();
            targetRule.setPeriodId(targetPeriodId);
            targetRule.setRuleName(sourceRule.getRuleName() + "（复制）");
            targetRule.setVersionCode(StringUtils.hasText(sourceRule.getVersionCode()) ? sourceRule.getVersionCode() : "v1");
            targetRule.setStatus(1);
            ruleMapper.insertRule(targetRule);
        }
        int existing = ruleCategoryMapper.countByRuleId(targetRule.getId());
        if (!Boolean.TRUE.equals(overwrite) && existing > 0) {
            throw new IllegalStateException("目标学期规则下已存在分类，请勾选「覆盖」后再复制，或先手动清理目标分类");
        }
        if (Boolean.TRUE.equals(overwrite)) {
            ruleCategoryMapper.deleteByRuleId(targetRule.getId());
        }
        List<RuleCategory> sourceCats = ruleCategoryMapper.listByRuleId(sourceRule.getId());
        if (sourceCats.isEmpty()) {
            return 0;
        }
        List<RuleCategory> ordered = RuleCategoryTreeUtils.topologicalOrder(sourceCats);
        int n = 0;
        Map<Integer, Integer> oldToNew = new HashMap<>(ordered.size() * 2);
        for (RuleCategory c : ordered) {
            int sp = RuleCategoryTreeUtils.normParentId(c.getParentId());
            int newParent = sp == 0 ? 0 : oldToNew.get(sp);
            if (sp != 0 && !oldToNew.containsKey(sp)) {
                throw new IllegalStateException("规则分类树数据异常：存在悬空父级");
            }
            AddRuleCategoryReq req = new AddRuleCategoryReq();
            req.setRuleId(targetRule.getId());
            req.setCategoryName(c.getCategoryName());
            req.setParentId(newParent);
            req.setScoreCap(c.getScoreCap());
            req.setStudentVisible(c.getStudentVisible() != null ? c.getStudentVisible() : 1);
            req.setSortOrder(c.getSortOrder() != null ? c.getSortOrder() : 0);
            req.setCategoryBaseScore(c.getCategoryBaseScore() != null ? c.getCategoryBaseScore() : java.math.BigDecimal.ZERO);
            ruleCategoryMapper.addRuleCategory(req);
            if (req.getId() == null) {
                throw new IllegalStateException("插入分类失败：未生成主键");
            }
            oldToNew.put(c.getId(), req.getId());
            n++;
        }
        return n;
    }

    @Override
    public RuleCategoryCopyPreviewVO previewCopyByPeriod(Integer sourcePeriodId, Integer targetPeriodId) {
        if (sourcePeriodId == null || sourcePeriodId <= 0 || targetPeriodId == null || targetPeriodId <= 0) {
            throw new IllegalArgumentException("请选择有效学期");
        }
        Rule sourceRule = ruleMapper.findLatestByPeriodId(sourcePeriodId);
        Rule targetRule = ruleMapper.findLatestByPeriodId(targetPeriodId);
        if (sourceRule == null || sourceRule.getId() == null) {
            throw new IllegalStateException("来源学期暂无规则可复制");
        }
        int sourceTotal = ruleCategoryMapper.countByRuleId(sourceRule.getId());
        int targetExistingTotal =
                (targetRule == null || targetRule.getId() == null) ? 0 : ruleCategoryMapper.countByRuleId(targetRule.getId());
        RuleCategoryCopyPreviewVO vo = new RuleCategoryCopyPreviewVO();
        vo.setSourcePeriodId(sourcePeriodId);
        vo.setTargetPeriodId(targetPeriodId);
        vo.setSourceTotal(sourceTotal);
        vo.setTargetExistingTotal(targetExistingTotal);
        return vo;
    }
}
