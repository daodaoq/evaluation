package com.project.evaluation.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Rule;
import com.project.evaluation.mapper.RuleMapper;
import com.project.evaluation.service.RuleService;
import com.project.evaluation.vo.Rule.AddRuleReq;
import com.project.evaluation.vo.Rule.UpdateRuleReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class RuleServiceImpl implements RuleService {

    @Autowired
    private RuleMapper ruleMapper;

    /**
     * 添加规则总览
     * @param addRuleReq
     */
    @Override
    public void addRule(AddRuleReq addRuleReq) {
        ruleMapper.addRule(addRuleReq);
        log.info("添加成功：{}",addRuleReq);
    }

    /**
     * 删除规则总览
     * @param id
     */
    @Override
    public void deleteRule(Integer id)  {
        if(id == null || id <= 0){
            throw new IllegalArgumentException("非法规则总览ID");
        }
        int rows = ruleMapper.deleteRule(id);
        if(rows == 0)
        {
            log.warn("删除失败，规则总览id不存在：{}",id);
            throw new IllegalStateException("规则总览不存在或已删除");
        }
        log.info("删除规则总览成功： id={}",id);

    }

    /**
     * 更新规则总览信息
     * @param updateRuleReq
     */
    @Override
    public void updateRule(Integer id, UpdateRuleReq updateRuleReq) {
        ruleMapper.updateRule(id, updateRuleReq);
    }

    /**
     * 通过周其名称查找规则总览
     * @param name
     * @return Rule
     */
    @Override
    public Rule findRuleByName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("规则总览名称不能为空");
        }
        return ruleMapper.findRuleByName(name.trim());
    }

    /**
     * 通过 id 查找规则总览
     * @param id
     * @return
     */
    @Override
    public Rule findRuleById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("非法规则总览ID");
        }
        return ruleMapper.findRuleById(id);
    }

    /**
     * 批量获取规则总览
     * @return
     */
    @Override
    public List<Rule> ruleList() {
        return ruleMapper.ruleList();
    }

    /**
     * 分页条件查询规则总览
     * @param pageNum
     * @param pageSize
     * @param periodId
     * @param status
     * @return
     */
    @Override
    public PageBean<Rule> paginationQuery(Integer pageNum, Integer pageSize, List<Integer> periodIds, List<Integer> statuses) {
        PageBean<Rule> pb = new PageBean<>();

        PageHelper.startPage(pageNum, pageSize);

        List<Rule> rules = ruleMapper.paginationQuery(periodIds, statuses);

        PageInfo<Rule> info = new PageInfo<>(rules);
        pb.setTotal(info.getTotal());
        pb.setItems(info.getList());
        return pb;
    }
}
