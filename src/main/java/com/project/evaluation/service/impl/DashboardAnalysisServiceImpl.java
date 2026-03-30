package com.project.evaluation.service.impl;

import com.project.evaluation.entity.Time;
import com.project.evaluation.mapper.DashboardMapper;
import com.project.evaluation.service.DashboardAnalysisService;
import com.project.evaluation.vo.Dashboard.DashboardAnalysisVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DashboardAnalysisServiceImpl implements DashboardAnalysisService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public DashboardAnalysisVO getDashboardAnalysis(Integer periodId) {
        DashboardAnalysisVO vo = new DashboardAnalysisVO();

        // 1) 周期总数概览（不依赖具体 periodId）
        vo.setPeriodSummary(dashboardMapper.selectPeriodSummary());

        // 2) 确定生效周期（优先使用参数；空则取最新启用且未归档的周期）
        Time latest = dashboardMapper.selectLatestActivePeriod();
        Integer effectivePeriodId = periodId != null ? periodId : (latest != null ? latest.getId() : null);

        if (effectivePeriodId == null) {
            vo.setPeriodId(null);
            vo.setPeriodName(null);
            vo.setApplyStatusCounts(Collections.emptyList());
            vo.setObjectionStatusCounts(Collections.emptyList());
            vo.setRuleItemTypeCounts(Collections.emptyList());
            return vo;
        }

        vo.setPeriodId(effectivePeriodId);

        Time period = dashboardMapper.selectPeriodById(effectivePeriodId);
        vo.setPeriodName(period != null ? period.getPeriodName() : (latest != null ? latest.getPeriodName() : null));

        // 3) 申报/异议状态分布
        List<DashboardAnalysisVO.StatusCount> applyCounts = dashboardMapper.selectApplyStatusCounts(effectivePeriodId);
        List<DashboardAnalysisVO.StatusCount> objectionCounts = dashboardMapper.selectObjectionStatusCounts(effectivePeriodId);
        vo.setApplyStatusCounts(applyCounts != null ? applyCounts : Collections.emptyList());
        vo.setObjectionStatusCounts(objectionCounts != null ? objectionCounts : Collections.emptyList());

        // 4) 规则项类型分布（加分/扣分）
        List<DashboardAnalysisVO.RuleItemTypeCount> typeCounts = dashboardMapper.selectRuleItemTypeCounts(effectivePeriodId);
        vo.setRuleItemTypeCounts(typeCounts != null ? typeCounts : Collections.emptyList());

        return vo;
    }
}

