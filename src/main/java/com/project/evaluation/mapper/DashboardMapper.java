package com.project.evaluation.mapper;

import com.project.evaluation.vo.Dashboard.DashboardAnalysisVO;
import com.project.evaluation.entity.Time;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DashboardMapper {

    @Select("""
        SELECT
          COUNT(1) AS totalPeriods,
          SUM(CASE WHEN status = 1 AND IFNULL(archived, 0) = 0 THEN 1 ELSE 0 END) AS activePeriods,
          SUM(CASE WHEN IFNULL(archived, 0) = 1 THEN 1 ELSE 0 END) AS archivedPeriods
        FROM evaluation_period
        """)
    DashboardAnalysisVO.PeriodSummary selectPeriodSummary();

    @Select("""
        SELECT
          id AS id,
          period_name AS periodName
        FROM evaluation_period
        WHERE status = 1 AND IFNULL(archived, 0) = 0
        ORDER BY id DESC
        LIMIT 1
        """)
    Time selectLatestActivePeriod();

    @Select("""
        SELECT
          id AS id,
          period_name AS periodName
        FROM evaluation_period
        WHERE id = #{periodId}
        """)
    Time selectPeriodById(@Param("periodId") Integer periodId);

    @Select("""
        SELECT
          status AS status,
          COUNT(1) AS count
        FROM evaluation_apply
        WHERE period_id = #{periodId}
        GROUP BY status
        """)
    List<DashboardAnalysisVO.StatusCount> selectApplyStatusCounts(@Param("periodId") Integer periodId);

    @Select("""
        SELECT
          status AS status,
          COUNT(1) AS count
        FROM evaluation_objection
        WHERE period_id = #{periodId}
        GROUP BY status
        """)
    List<DashboardAnalysisVO.StatusCount> selectObjectionStatusCounts(@Param("periodId") Integer periodId);

    @Select("""
        SELECT
          COALESCE(UPPER(ri.item_type), 'UNKNOWN') AS itemType,
          COUNT(1) AS count
        FROM evaluation_rule_item ri
        INNER JOIN evaluation_rule r ON ri.rule_id = r.id
        WHERE r.period_id = #{periodId}
          AND r.status = 1
          AND ri.status = 1
        GROUP BY COALESCE(UPPER(ri.item_type), 'UNKNOWN')
        ORDER BY count DESC
        """)
    List<DashboardAnalysisVO.RuleItemTypeCount> selectRuleItemTypeCounts(@Param("periodId") Integer periodId);

}

