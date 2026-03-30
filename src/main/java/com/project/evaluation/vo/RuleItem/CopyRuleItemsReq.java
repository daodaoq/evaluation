package com.project.evaluation.vo.RuleItem;

import lombok.Data;

@Data
public class CopyRuleItemsReq {
    private Integer sourcePeriodId;
    private Integer targetPeriodId;
    /**
     * true: 覆盖目标学期既有规则项
     */
    private Boolean overwrite;
}
