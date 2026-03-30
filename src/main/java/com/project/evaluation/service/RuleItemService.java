package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.RuleItem;
import com.project.evaluation.vo.RuleItem.AddRuleItemReq;
import com.project.evaluation.vo.RuleItem.RuleCopyPreviewVO;
import com.project.evaluation.vo.RuleItem.UpdateRuleItemReq;

import java.util.List;

public interface RuleItemService {
    RuleItem findRuleItemByName(String periodName);

    void addRuleItem(AddRuleItemReq addRuleItemReq);

    void deleteRuleItem(Integer id);

    RuleItem findRuleItemById(Integer id);

    void updateRuleItem(Integer id, UpdateRuleItemReq updateRuleItemReq);

    List<RuleItem> ruleItemList();

    PageBean<RuleItem> paginationQuery(Integer pageNum, Integer pageSize);

    List<RuleItem> listByPeriod(Integer periodId, String moduleCode, Integer itemCategory);

    int copyByPeriod(Integer sourcePeriodId, Integer targetPeriodId, Boolean overwrite);

    RuleCopyPreviewVO previewCopyByPeriod(Integer sourcePeriodId, Integer targetPeriodId);
}
