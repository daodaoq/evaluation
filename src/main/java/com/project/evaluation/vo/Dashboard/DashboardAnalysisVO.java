package com.project.evaluation.vo.Dashboard;

import lombok.Data;

import java.util.List;

/**
 * 首页数据分析统计返回值
 */
@Data
public class DashboardAnalysisVO {

    private Integer periodId;
    private String periodName;

    private PeriodSummary periodSummary;

    private List<StatusCount> applyStatusCounts;
    private List<StatusCount> objectionStatusCounts;

    private List<RuleItemTypeCount> ruleItemTypeCounts;

    @Data
    public static class PeriodSummary {
        private long totalPeriods;
        private long activePeriods;
        private long archivedPeriods;
    }

    @Data
    public static class StatusCount {
        private String status;
        private long count;
    }

    @Data
    public static class RuleItemTypeCount {
        private String itemType;
        private long count;
    }
}

