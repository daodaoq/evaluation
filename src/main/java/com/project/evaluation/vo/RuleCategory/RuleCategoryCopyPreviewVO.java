package com.project.evaluation.vo.RuleCategory;

import lombok.Data;

@Data
public class RuleCategoryCopyPreviewVO {
    private Integer sourcePeriodId;
    private Integer targetPeriodId;
    private int sourceTotal;
    private int targetExistingTotal;
}
