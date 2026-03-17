package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Rule;
import com.project.evaluation.vo.Rule.AddRuleReq;
import com.project.evaluation.vo.Rule.UpdateRuleReq;

import java.util.List;

public interface RuleService {
    Rule findRuleByName(String periodName);

    void addRule(AddRuleReq addRuleReq);

    void deleteRule(Integer id);

    Rule findRuleById(Integer id);

    void updateRule(Integer id, UpdateRuleReq updateRuleReq);

    List<Rule> ruleList();

    PageBean<Rule> paginationQuery(Integer pageNum, Integer pageSize, Integer periodId, Integer status);
}
