package com.project.evaluation.vo.RuleItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class RuleCopyPreviewVO {
    private Integer sourcePeriodId;
    private Integer targetPeriodId;
    private Integer sourceTotal;
    private Integer targetExistingTotal;
    private List<CategoryCount> categoryCounts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryCount {
        private Integer itemCategory;
        private Integer count;
    }
}
