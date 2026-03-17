package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.RuleCategory;
import com.project.evaluation.vo.RuleCategory.AddRuleCategoryReq;
import com.project.evaluation.vo.RuleCategory.UpdateRuleCategoryReq;

import java.util.List;

public interface RuleCategoryService {
    RuleCategory findRuleCategoryByName(String periodName);

    void addRuleCategory(AddRuleCategoryReq addRuleCategoryReq);

    void deleteRuleCategory(Integer id);

    RuleCategory findRuleCategoryById(Integer id);

    void updateRuleCategory(Integer id, UpdateRuleCategoryReq updateRuleCategoryReq);

    List<RuleCategory> ruleCategoryList();

    PageBean<RuleCategory> paginationQuery(Integer pageNum, Integer pageSize);
}
