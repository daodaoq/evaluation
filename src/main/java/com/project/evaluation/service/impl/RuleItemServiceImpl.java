package com.project.evaluation.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Rule;
import com.project.evaluation.entity.RuleItem;
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
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RuleItemServiceImpl implements RuleItemService {

    @Autowired
    private RuleItemMapper ruleItemMapper;

    @Autowired
    private RuleMapper ruleMapper;

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
            targetRule.setRuleName(sourceRule.getRuleName() + "（复制）");
            targetRule.setVersionCode(StringUtils.hasText(sourceRule.getVersionCode()) ? sourceRule.getVersionCode() : "v1");
            targetRule.setStatus(1);
            targetRule.setMoralWeight(sourceRule.getMoralWeight());
            targetRule.setAcademicWeight(sourceRule.getAcademicWeight());
            targetRule.setQualityWeight(sourceRule.getQualityWeight());
            ruleMapper.insertRule(targetRule);
        }
        if (Boolean.TRUE.equals(overwrite)) {
            ruleItemMapper.deleteByRuleId(targetRule.getId());
        }
        return ruleItemMapper.copyByRuleId(sourceRule.getId(), targetRule.getId());
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
