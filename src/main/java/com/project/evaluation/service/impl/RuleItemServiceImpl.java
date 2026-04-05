package com.project.evaluation.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Rule;
import com.project.evaluation.entity.RuleCategory;
import com.project.evaluation.entity.RuleItem;
import com.project.evaluation.mapper.RuleCategoryMapper;
import com.project.evaluation.mapper.RuleMapper;
import com.project.evaluation.mapper.RuleItemMapper;
import com.project.evaluation.service.RuleItemService;
import com.project.evaluation.vo.RuleItem.AddRuleItemReq;
import com.project.evaluation.vo.RuleItem.RuleCopyPreviewVO;
import com.project.evaluation.vo.RuleItem.UpdateRuleItemReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RuleItemServiceImpl implements RuleItemService {

    @Autowired
    private RuleItemMapper ruleItemMapper;

    @Autowired
    private RuleMapper ruleMapper;

    @Autowired
    private RuleCategoryMapper ruleCategoryMapper;

    /**
     * 添加规则项
     * @param addRuleItemReq
     */
    @Override
    public void addRuleItem(AddRuleItemReq addRuleItemReq) {
        if (!StringUtils.hasText(addRuleItemReq.getScoreMode())) {
            addRuleItemReq.setScoreMode("ADD");
        }
        if (addRuleItemReq.getCoeff() == null) {
            addRuleItemReq.setCoeff(new BigDecimal("1.000"));
        }
        ruleItemMapper.addRuleItem(addRuleItemReq);
        log.info("添加成功：{}",addRuleItemReq);
    }

    /**
     * 删除规则项
     * @param id
     */
    @Override
    public void deleteRuleItem(Integer id)  {
        if(id == null || id <= 0){
            throw new IllegalArgumentException("非法规则项ID");
        }
        int rows = ruleItemMapper.deleteRuleItem(id);
        if(rows == 0)
        {
            log.warn("删除失败，规则项id不存在：{}",id);
            throw new IllegalStateException("规则项不存在或已删除");
        }
        log.info("删除规则项成功： id={}",id);

    }

    /**
     * 更新规则项信息
     * @param updateRuleItemReq
     */
    @Override
    public void updateRuleItem(Integer id, UpdateRuleItemReq updateRuleItemReq) {
        RuleItem existing = ruleItemMapper.findRuleItemById(id);
        if (existing != null) {
            if (!StringUtils.hasText(updateRuleItemReq.getScoreMode())) {
                updateRuleItemReq.setScoreMode(
                        StringUtils.hasText(existing.getScoreMode()) ? existing.getScoreMode() : "ADD");
            }
            if (updateRuleItemReq.getCoeff() == null) {
                updateRuleItemReq.setCoeff(
                        existing.getCoeff() != null ? existing.getCoeff() : new BigDecimal("1.000"));
            }
        }
        ruleItemMapper.updateRuleItem(id, updateRuleItemReq);
    }

    /**
     * 通过周其名称查找规则项
     * @param name
     * @return RuleItem
     */
    @Override
    public RuleItem findRuleItemByName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("规则项名称不能为空");
        }
        return ruleItemMapper.findRuleItemByName(name.trim());
    }

    /**
     * 通过 id 查找规则项
     * @param id
     * @return
     */
    @Override
    public RuleItem findRuleItemById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("非法规则项ID");
        }
        return ruleItemMapper.findRuleItemById(id);
    }

    /**
     * 批量获取规则项
     * @return
     */
    @Override
    public List<RuleItem> ruleItemList() {
        return ruleItemMapper.ruleItemList();
    }

    /**
     * 分页条件查询规则项
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageBean<RuleItem> paginationQuery(Integer pageNum, Integer pageSize) {
        PageBean<RuleItem> pb = new PageBean<>();

        PageHelper.startPage(pageNum, pageSize);

        List<RuleItem> ruleItems = ruleItemMapper.paginationQuery();

        PageInfo<RuleItem> info = new PageInfo<>(ruleItems);
        pb.setTotal(info.getTotal());
        pb.setItems(info.getList());
        return pb;
    }

    @Override
    public List<RuleItem> listByPeriod(Integer periodId, String moduleCode, Integer itemCategory) {
        if (periodId == null || periodId <= 0) {
            throw new IllegalArgumentException("请选择学期");
        }
        return ruleItemMapper.listByPeriod(periodId, moduleCode, itemCategory);
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
            targetRule.setRuleName(sourceRule.getRuleName() + "(复制)");
            targetRule.setVersionCode(StringUtils.hasText(sourceRule.getVersionCode()) ? sourceRule.getVersionCode() : "v1");
            targetRule.setStatus(1);
            ruleMapper.insertRule(targetRule);
        }
        if (Boolean.TRUE.equals(overwrite)) {
            ruleItemMapper.deleteByRuleId(targetRule.getId());
        }
        List<RuleItem> items = ruleItemMapper.listByRuleId(sourceRule.getId());
        if (items.isEmpty()) {
            return 0;
        }
        List<RuleCategory> srcCats = ruleCategoryMapper.listByRuleId(sourceRule.getId());
        List<RuleCategory> tgtCats = ruleCategoryMapper.listByRuleId(targetRule.getId());
        Map<Integer, Integer> catMap;
        if (srcCats.isEmpty()) {
            boolean orphan = false;
            for (RuleItem one : items) {
                Integer cat = one.getItemCategory();
                if (cat != null && cat != 0) {
                    orphan = true;
                    break;
                }
            }
            if (orphan) {
                throw new IllegalStateException("来源规则项引用了分类，但来源规则下没有分类数据，请先在规则分类管理中维护分类");
            }
            catMap = Collections.emptyMap();
        } else {
            catMap = RuleCategoryTreeUtils.buildSourceToTargetIdMap(srcCats, tgtCats);
        }
        int n = 0;
        for (RuleItem item : items) {
            insertRuleItemCopy(item, targetRule.getId(), catMap);
            n++;
        }
        return n;
    }

    /**
     * @param row 来源规则下的规则项（仅读取字段，不写回）
     */
    private void insertRuleItemCopy(RuleItem row, Integer targetRuleId, Map<Integer, Integer> catMap) {
        AddRuleItemReq req = new AddRuleItemReq();
        req.setRuleId(targetRuleId == null ? null : Long.valueOf(targetRuleId));
        req.setItemName(row.getItemName());
        req.setItemType(row.getItemType());
        Integer ic = row.getItemCategory();
        if (ic == null || ic == 0) {
            req.setItemCategory(0);
        } else {
            Integer mapped = catMap.get(ic);
            if (mapped == null) {
                throw new IllegalStateException("规则项引用的分类无法对齐到目标学期，请先在规则分类管理中从来源学期复制分类（建议勾选覆盖）");
            }
            req.setItemCategory(mapped);
        }
        req.setLevel(row.getLevel());
        req.setBaseScore(row.getBaseScore());
        req.setIsCompetition(row.getIsCompetition());
        req.setNeedMaterial(row.getNeedMaterial());
        req.setStatus(row.getStatus());
        req.setScoreMode(StringUtils.hasText(row.getScoreMode()) ? row.getScoreMode() : "ADD");
        req.setDedupeGroup(row.getDedupeGroup());
        BigDecimal coeffVal = row.getCoeff();
        if (coeffVal == null) {
            coeffVal = new BigDecimal("1.000");
        }
        req.setCoeff(coeffVal);
        req.setModuleCode(row.getModuleCode());
        req.setSubmoduleCode(row.getSubmoduleCode());
        ruleItemMapper.addRuleItem(req);
    }

    @Override
    public RuleCopyPreviewVO previewCopyByPeriod(Integer sourcePeriodId, Integer targetPeriodId) {
        if (sourcePeriodId == null || sourcePeriodId <= 0 || targetPeriodId == null || targetPeriodId <= 0) {
            throw new IllegalArgumentException("请选择有效学期");
        }
        Rule sourceRule = ruleMapper.findLatestByPeriodId(sourcePeriodId);
        Rule targetRule = ruleMapper.findLatestByPeriodId(targetPeriodId);
        if (sourceRule == null || sourceRule.getId() == null) {
            throw new IllegalStateException("来源学期暂无规则可复制");
        }
        int sourceTotal = ruleItemMapper.countByRuleId(sourceRule.getId());
        int targetExistingTotal = (targetRule == null || targetRule.getId() == null) ? 0 : ruleItemMapper.countByRuleId(targetRule.getId());
        List<Map<String, Object>> rows = ruleItemMapper.countByCategory(sourceRule.getId());
        List<RuleCopyPreviewVO.CategoryCount> categoryCounts = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Integer category = row.get("itemCategory") == null ? -1 : Integer.parseInt(String.valueOf(row.get("itemCategory")));
            Integer cnt = row.get("cnt") == null ? 0 : Integer.parseInt(String.valueOf(row.get("cnt")));
            categoryCounts.add(new RuleCopyPreviewVO.CategoryCount(category, cnt));
        }
        RuleCopyPreviewVO vo = new RuleCopyPreviewVO();
        vo.setSourcePeriodId(sourcePeriodId);
        vo.setTargetPeriodId(targetPeriodId);
        vo.setSourceTotal(sourceTotal);
        vo.setTargetExistingTotal(targetExistingTotal);
        vo.setCategoryCounts(categoryCounts);
        return vo;
    }
}
