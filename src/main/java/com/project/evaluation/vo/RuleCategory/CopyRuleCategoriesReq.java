package com.project.evaluation.vo.RuleCategory;

import lombok.Data;

@Data
public class CopyRuleCategoriesReq {
    private Integer sourcePeriodId;
    private Integer targetPeriodId;
    /**
     * true：删除目标规则下已有分类后再复制
     */
    private Boolean overwrite;
}
