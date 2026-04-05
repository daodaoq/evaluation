package com.project.evaluation.mapper;

import com.project.evaluation.vo.ClassScore.ApprovedRuleItemScoreRow;
import com.project.evaluation.vo.ClassScore.ClassEvaluationStudentRow;
import com.project.evaluation.vo.ClassScore.ClassUnsubmittedRowVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClassEvaluationScoreMapper {

    @Select("""
        <script>
        SELECT
            u.id AS userId,
            u.student_id AS studentNo,
            u.real_name AS studentName,
            u.class_id AS classId,
            c.class_name AS className,
            eas.intellectual_score AS intellectualScore
        FROM sys_user u
        INNER JOIN sys_user_role ur ON ur.user_id = u.id AND ur.role_id = 1
        LEFT JOIN sys_class c ON c.id = u.class_id
        LEFT JOIN evaluation_student_academic_score eas
            ON eas.student_no = u.student_id AND eas.period_id = #{periodId}
        WHERE u.status = 1
        <if test="classIds != null and classIds.size() &gt; 0">
            AND u.class_id IN
            <foreach collection="classIds" item="cid" open="(" separator="," close=")">#{cid}</foreach>
        </if>
        <if test="classId != null">
            AND u.class_id = #{classId}
        </if>
        <if test="studentNo != null and studentNo != ''">
            AND u.student_id LIKE CONCAT('%', #{studentNo}, '%')
        </if>
        ORDER BY u.student_id
        </script>
        """)
    List<ClassEvaluationStudentRow> listStudentsForScore(
            @Param("periodId") Long periodId,
            @Param("classIds") List<Integer> classIds,
            @Param("classId") Long classId,
            @Param("studentNo") String studentNo);

    @Select("""
        <script>
        SELECT
            a.student_id AS studentUserId,
            ai.id AS applyItemId,
            ri.id AS ruleItemId,
            COALESCE(ri.item_name, ai.custom_name) AS itemName,
            COALESCE(ri.module_code,
                CASE WHEN ai.source_type = 'CUSTOM' AND TRIM(IFNULL(ai.custom_name,'')) = '任职分' THEN 'MORAL' END
            ) AS moduleCode,
            ri.submodule_code AS submoduleCode,
            ri.level AS level,
            ri.base_score AS baseScore,
            ri.coeff AS coeff,
            ri.score_mode AS scoreMode,
            ri.dedupe_group AS dedupeGroup,
            IFNULL(ai.score, 0) AS persistedScore,
            ai.source_type AS sourceType,
            ri.item_category AS itemCategory
        FROM evaluation_apply_item ai
        INNER JOIN evaluation_apply a ON ai.apply_id = a.id
        INNER JOIN sys_user u ON a.student_id = u.id
        LEFT JOIN evaluation_rule_item ri ON ai.rule_item_id = ri.id
        WHERE a.period_id = #{periodId}
          AND ai.status = 'APPROVED'
          AND a.id = (
            SELECT MAX(a2.id) FROM evaluation_apply a2
            WHERE a2.student_id = a.student_id AND a2.period_id = a.period_id
          )
          AND (
            (ai.source_type = 'RULE' AND ri.id IS NOT NULL)
            OR (ai.source_type = 'CUSTOM' AND TRIM(IFNULL(ai.custom_name,'')) = '任职分')
          )
        <if test="studentUserIds != null and studentUserIds.size() &gt; 0">
          AND a.student_id IN
          <foreach collection="studentUserIds" item="sid" open="(" separator="," close=")">#{sid}</foreach>
        </if>
        <if test="classIds != null and classIds.size() &gt; 0">
          AND u.class_id IN
          <foreach collection="classIds" item="cid" open="(" separator="," close=")">#{cid}</foreach>
        </if>
        <if test="classId != null">
          AND u.class_id = #{classId}
        </if>
        </script>
        """)
    List<ApprovedRuleItemScoreRow> listApprovedRuleItemsForPeriod(
            @Param("periodId") Long periodId,
            @Param("studentUserIds") List<Long> studentUserIds,
            @Param("classIds") List<Integer> classIds,
            @Param("classId") Long classId);

    @Select("""
        <script>
        SELECT
            u.id AS userId,
            u.student_id AS studentNo,
            u.real_name AS studentName,
            u.class_id AS classId,
            c.class_name AS className
        FROM sys_user u
        INNER JOIN sys_user_role ur ON ur.user_id = u.id AND ur.role_id = 1
        LEFT JOIN sys_class c ON c.id = u.class_id
        WHERE u.status = 1
        <if test="classIds != null and classIds.size() &gt; 0">
            AND u.class_id IN
            <foreach collection="classIds" item="cid" open="(" separator="," close=")">#{cid}</foreach>
        </if>
        <if test="classId != null">
            AND u.class_id = #{classId}
        </if>
        <if test="studentNo != null and studentNo != ''">
            AND u.student_id LIKE CONCAT('%', #{studentNo}, '%')
        </if>
        AND NOT EXISTS (
            SELECT 1 FROM evaluation_apply a
            WHERE a.student_id = u.id
              AND a.period_id = #{periodId}
              AND a.status = 'SUBMITTED'
        )
        ORDER BY u.student_id
        </script>
        """)
    List<ClassUnsubmittedRowVO> listUnsubmittedStudents(
            @Param("periodId") Long periodId,
            @Param("classIds") List<Integer> classIds,
            @Param("classId") Long classId,
            @Param("studentNo") String studentNo);
}
