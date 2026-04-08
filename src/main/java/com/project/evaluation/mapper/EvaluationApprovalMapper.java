package com.project.evaluation.mapper;

import com.project.evaluation.vo.EvaluationApproval.ApplyItemMaterialJoinVO;
import com.project.evaluation.vo.EvaluationApproval.ApplyItemScoringSnapshot;
import com.project.evaluation.vo.EvaluationApproval.EvaluationApplyItemVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface EvaluationApprovalMapper {

    @Select("""
        <script>
        SELECT
            a.id AS applyId,
            ai.id AS applyItemId,
            a.student_id AS studentId,
            u.student_id AS studentNo,
            u.real_name AS studentName,
            u.college_id AS collegeId,
            c.college_name AS collegeName,
            u.class_id AS classId,
            cl.class_name AS className,
            a.period_id AS periodId,
            a.status AS applyStatus,
            ai.status AS itemStatus,
            ai.rule_item_id AS ruleItemId,
            ri.item_name AS ruleItemName,
            ai.custom_name AS customName,
            ai.remark AS itemRemark,
            ai.score AS score,
            ar.auditor_id AS auditorId,
            au.student_id AS auditorNo,
            au.real_name AS auditorName,
            a.create_time AS applyCreateTime,
            a.update_time AS applyUpdateTime,
            ai.create_time AS itemCreateTime
        FROM evaluation_apply_item ai
        INNER JOIN evaluation_apply a ON ai.apply_id = a.id
        INNER JOIN sys_user u ON a.student_id = u.id
        LEFT JOIN sys_college c ON u.college_id = c.id
        LEFT JOIN sys_class cl ON u.class_id = cl.id
        LEFT JOIN evaluation_rule_item ri ON ai.rule_item_id = ri.id
        LEFT JOIN (
            SELECT t1.apply_item_id, t1.auditor_id
            FROM evaluation_audit_record t1
            INNER JOIN (
                SELECT apply_item_id, MAX(create_time) AS mx
                FROM evaluation_audit_record
                GROUP BY apply_item_id
            ) t2 ON t1.apply_item_id = t2.apply_item_id AND t1.create_time = t2.mx
        ) ar ON ar.apply_item_id = ai.id
        LEFT JOIN sys_user au ON au.id = ar.auditor_id
        <where>
            <if test='studentNo != null and studentNo != ""'>
                AND u.student_id LIKE CONCAT('%', #{studentNo}, '%')
            </if>
            <if test='periodIds != null and periodIds.size() &gt; 0'>
                AND a.period_id IN
                <foreach collection="periodIds" item="pid" open="(" separator="," close=")">#{pid}</foreach>
            </if>
            <if test='applyStatuses != null and applyStatuses.size() &gt; 0'>
                AND a.status IN
                <foreach collection="applyStatuses" item="st" open="(" separator="," close=")">#{st}</foreach>
            </if>
            <if test='itemStatuses != null and itemStatuses.size() &gt; 0'>
                AND ai.status IN
                <foreach collection="itemStatuses" item="st" open="(" separator="," close=")">#{st}</foreach>
            </if>
            <if test='collegeId != null'>
                AND u.college_id = #{collegeId}
            </if>
            <if test='classId != null'>
                AND u.class_id = #{classId}
            </if>
        </where>
        ORDER BY ai.id DESC
        </script>
        """)
    List<EvaluationApplyItemVO> pageApplyItems(
            @Param("studentNo") String studentNo,
            @Param("periodIds") List<Long> periodIds,
            @Param("applyStatuses") List<String> applyStatuses,
            @Param("itemStatuses") List<String> itemStatuses,
            @Param("collegeId") Long collegeId,
            @Param("classId") Long classId);

    @Select("""
            <script>
            SELECT apply_item_id AS applyItemId, file_name AS fileName, file_url AS fileUrl
            FROM evaluation_apply_material
            WHERE apply_item_id IN
            <foreach collection="ids" item="id" open="(" separator="," close=")">#{id}</foreach>
            ORDER BY apply_item_id ASC, id ASC
            </script>
            """)
    List<ApplyItemMaterialJoinVO> listMaterialsByApplyItemIds(@Param("ids") List<Long> ids);

    @Select("SELECT COUNT(1) FROM evaluation_apply_material WHERE file_url = #{key}")
    int countMaterialByFileUrl(@Param("key") String key);

    @Update("UPDATE evaluation_apply_item SET status = #{status} WHERE id = #{applyItemId}")
    int updateApplyItemStatus(@Param("applyItemId") Long applyItemId, @Param("status") String status);

    @Update("UPDATE evaluation_apply_item SET status = #{status}, score = #{score} WHERE id = #{applyItemId}")
    int updateApplyItemStatusAndScore(
            @Param("applyItemId") Long applyItemId,
            @Param("status") String status,
            @Param("score") BigDecimal score);

    @Select("""
            SELECT ai.id AS applyItemId,
                   ai.source_type AS sourceType,
                   ai.rule_item_id AS ruleItemId,
                   IFNULL(ai.score, 0) AS persistedScore,
                   ri.base_score AS baseScore,
                   IFNULL(ri.coeff, 1) AS coeff,
                   ri.score_mode AS scoreMode
            FROM evaluation_apply_item ai
            LEFT JOIN evaluation_rule_item ri ON ai.rule_item_id = ri.id
            WHERE ai.id = #{applyItemId}
            LIMIT 1
            """)
    ApplyItemScoringSnapshot selectScoringSnapshot(@Param("applyItemId") Long applyItemId);

    @Select("""
            <script>
            SELECT ai.id AS applyItemId,
                   ai.source_type AS sourceType,
                   ai.rule_item_id AS ruleItemId,
                   IFNULL(ai.score, 0) AS persistedScore,
                   ri.base_score AS baseScore,
                   IFNULL(ri.coeff, 1) AS coeff,
                   ri.score_mode AS scoreMode
            FROM evaluation_apply_item ai
            LEFT JOIN evaluation_rule_item ri ON ai.rule_item_id = ri.id
            WHERE ai.id IN
            <foreach collection="ids" item="id" open="(" separator="," close=")">#{id}</foreach>
            </script>
            """)
    List<ApplyItemScoringSnapshot> selectScoringSnapshotsByApplyItemIds(@Param("ids") List<Long> ids);

    @Insert("""
        INSERT INTO evaluation_audit_record
        (apply_item_id, auditor_id, audit_result, remark, create_time)
        VALUES (#{applyItemId}, #{auditorId}, #{auditResult}, #{remark}, NOW())
        """)
    void insertAuditRecord(@Param("applyItemId") Long applyItemId,
                           @Param("auditorId") Long auditorId,
                           @Param("auditResult") String auditResult,
                           @Param("remark") String remark);

    @Select("SELECT apply_id FROM evaluation_apply_item WHERE id = #{applyItemId}")
    Long findApplyIdByApplyItemId(@Param("applyItemId") Long applyItemId);

    @Select("""
            SELECT a.period_id FROM evaluation_apply_item ai
            INNER JOIN evaluation_apply a ON ai.apply_id = a.id
            WHERE ai.id = #{applyItemId}
            """)
    Long findPeriodIdByApplyItemId(@Param("applyItemId") Long applyItemId);

    @Select("SELECT status FROM evaluation_apply_item WHERE id = #{applyItemId}")
    String findApplyItemStatusById(@Param("applyItemId") Long applyItemId);

    @Select("""
            SELECT u.class_id
            FROM evaluation_apply_item ai
            INNER JOIN evaluation_apply a ON ai.apply_id = a.id
            INNER JOIN sys_user u ON a.student_id = u.id
            WHERE ai.id = #{applyItemId}
            """)
    Integer findStudentClassIdByApplyItemId(@Param("applyItemId") Long applyItemId);

    @Select("SELECT COUNT(1) FROM evaluation_apply_item WHERE apply_id = #{applyId}")
    int countApplyItems(@Param("applyId") Long applyId);

    @Select("SELECT COUNT(1) FROM evaluation_apply_item WHERE apply_id = #{applyId} AND status = #{status}")
    int countApplyItemsByStatus(@Param("applyId") Long applyId, @Param("status") String status);

    @Update("UPDATE evaluation_apply SET status = #{status}, update_time = NOW() WHERE id = #{applyId}")
    void updateApplyStatus(@Param("applyId") Long applyId, @Param("status") String status);
}
