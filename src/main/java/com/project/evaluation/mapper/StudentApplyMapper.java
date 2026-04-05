package com.project.evaluation.mapper;

import com.project.evaluation.entity.EvaluationApply;
import com.project.evaluation.entity.EvaluationApplyItem;
import com.project.evaluation.entity.EvaluationApplyMaterial;
import com.project.evaluation.vo.StudentApply.MyApplyVO;
import com.project.evaluation.vo.StudentApply.RuleItemScoreMeta;
import com.project.evaluation.vo.StudentApply.RuleItemSimpleVO;
import com.project.evaluation.vo.StudentApply.StudentApplyApprovedScoreRow;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface StudentApplyMapper {

    @Select("""
        SELECT
            ri.id,
            ri.rule_id AS ruleId,
            ri.item_name AS itemName,
            ri.need_material AS needMaterial,
            ri.base_score AS baseScore,
            ri.item_type AS itemType,
            ri.item_category AS itemCategory,
            ri.level,
            ri.score_mode AS scoreMode,
            ri.module_code AS moduleCode,
            ri.coeff AS coeff
        FROM evaluation_rule_item ri
        INNER JOIN evaluation_rule r ON ri.rule_id = r.id
        WHERE r.period_id = #{periodId}
          AND r.status = 1
          AND ri.status = 1
          AND (
              ri.module_code IS NULL
              OR UPPER(ri.module_code) <> 'ACADEMIC'
              OR ri.item_name IN ('其他加分（突发·每次0.1）', '其他减分（突发·每次0.1）')
          )
        ORDER BY ri.id DESC
        """)
    List<RuleItemSimpleVO> listEnabledRuleItemsByPeriod(@Param("periodId") Long periodId);

    @Select("""
            SELECT COUNT(1) FROM evaluation_period
            WHERE id = #{periodId} AND status = 1 AND IFNULL(archived, 0) = 0
            """)
    int countActivePeriod(@Param("periodId") Long periodId);

    @Select("SELECT need_material FROM evaluation_rule_item WHERE id = #{ruleItemId} LIMIT 1")
    Integer findNeedMaterialByRuleItemId(@Param("ruleItemId") Long ruleItemId);

    @Select("SELECT module_code FROM evaluation_rule_item WHERE id = #{ruleItemId} LIMIT 1")
    String findModuleCodeByRuleItemId(@Param("ruleItemId") Long ruleItemId);

    @Select("SELECT item_name FROM evaluation_rule_item WHERE id = #{ruleItemId} LIMIT 1")
    String findItemNameByRuleItemId(@Param("ruleItemId") Long ruleItemId);

    @Insert("""
        INSERT INTO evaluation_apply
        (student_id, period_id, status, total_score, create_time, update_time)
        VALUES (#{studentId}, #{periodId}, #{status}, #{totalScore}, NOW(), NOW())
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertApply(EvaluationApply apply);

    @Insert("""
        INSERT INTO evaluation_apply_item
        (apply_id, rule_item_id, score, status, source_type, custom_name, remark, create_time)
        VALUES (#{applyId}, #{ruleItemId}, #{score}, #{status}, #{sourceType}, #{customName}, #{remark}, NOW())
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertApplyItem(EvaluationApplyItem item);

    @Insert("""
        INSERT INTO evaluation_apply_material
        (apply_item_id, file_name, file_url, create_time)
        VALUES (#{applyItemId}, #{fileName}, #{fileUrl}, NOW())
        """)
    int insertApplyMaterial(EvaluationApplyMaterial material);

    @Select("""
        SELECT
            a.id AS applyId,
            a.period_id AS periodId,
            a.status AS applyStatus,
            a.total_score AS totalScore,
            a.create_time AS createTime,
            ai.id AS applyItemId,
            ai.status AS itemStatus,
            ai.rule_item_id AS ruleItemId,
            ri.item_name AS itemName,
            ai.source_type AS sourceType,
            ai.custom_name AS customName,
            ai.remark AS remark,
            lap.id AS appealId,
            lap.status AS appealStatus,
            lap.reason AS appealReason,
            lap.handler_remark AS appealHandlerRemark
        FROM evaluation_apply a
        INNER JOIN evaluation_apply_item ai ON ai.apply_id = a.id
        LEFT JOIN evaluation_rule_item ri ON ai.rule_item_id = ri.id
        LEFT JOIN (
            SELECT x.id, x.apply_item_id, x.status, x.reason, x.handler_remark
            FROM evaluation_apply_item_appeal x
            INNER JOIN (
                SELECT apply_item_id, MAX(id) AS max_id
                FROM evaluation_apply_item_appeal
                GROUP BY apply_item_id
            ) m ON m.max_id = x.id
        ) lap ON lap.apply_item_id = ai.id
        WHERE a.student_id = #{studentUserId}
        ORDER BY a.id DESC, ai.id DESC
        """)
    List<MyApplyVO> listMyApplyItems(@Param("studentUserId") Long studentUserId);

    @Select("""
            <script>
            SELECT id, apply_item_id AS applyItemId, file_name AS fileName, file_url AS fileUrl
            FROM evaluation_apply_material
            WHERE apply_item_id IN
            <foreach collection="itemIds" item="id" open="(" separator="," close=")">
                #{id}
            </foreach>
            ORDER BY apply_item_id ASC, id ASC
            </script>
            """)
    List<EvaluationApplyMaterial> listMaterialsByApplyItemIds(@Param("itemIds") Collection<Long> itemIds);

    @Select("""
            SELECT ai.id AS applyItemId,
                   ri.id AS ruleItemId,
                   COALESCE(ri.item_name, ai.custom_name) AS itemName,
                   ri.level AS level,
                   ri.dedupe_group AS dedupeGroup,
                   IFNULL(ai.score, 0) AS score,
                   ai.source_type AS sourceType,
                   ai.custom_name AS customName,
                   UPPER(IFNULL(ri.module_code, '')) AS moduleCode,
                   UPPER(IFNULL(ri.submodule_code, '')) AS submoduleCode,
                   ri.base_score AS baseScore,
                   IFNULL(ri.coeff, 1) AS coeff,
                   ri.score_mode AS scoreMode,
                   ri.item_category AS itemCategory
            FROM evaluation_apply a
            INNER JOIN evaluation_apply_item ai ON ai.apply_id = a.id
            LEFT JOIN evaluation_rule_item ri ON ai.rule_item_id = ri.id
            WHERE a.student_id = #{studentUserId}
              AND a.period_id = #{periodId}
              AND ai.status = 'APPROVED'
              AND a.id = (
                  SELECT MAX(a2.id) FROM evaluation_apply a2
                  WHERE a2.student_id = a.student_id AND a2.period_id = a.period_id
              )
            """)
    List<StudentApplyApprovedScoreRow> listApprovedScoresForPeriod(
            @Param("studentUserId") Long studentUserId,
            @Param("periodId") Long periodId);

    @Select("SELECT item_category FROM evaluation_rule_item WHERE id = #{ruleItemId} LIMIT 1")
    Integer findItemCategoryByRuleItemId(@Param("ruleItemId") Long ruleItemId);

    @Select("SELECT rule_id FROM evaluation_rule_item WHERE id = #{ruleItemId} LIMIT 1")
    Integer findRuleIdByRuleItemId(@Param("ruleItemId") Long ruleItemId);

    @Select("""
            SELECT ri.base_score AS baseScore, IFNULL(ri.coeff, 1) AS coeff, ri.score_mode AS scoreMode
            FROM evaluation_rule_item ri
            WHERE ri.id = #{ruleItemId}
            LIMIT 1
            """)
    RuleItemScoreMeta selectRuleItemScoreMeta(@Param("ruleItemId") Long ruleItemId);
}
