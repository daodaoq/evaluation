package com.project.evaluation.mapper;

import com.project.evaluation.vo.EvaluationApproval.EvaluationApplyItemVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
            ai.score AS score,
            a.create_time AS applyCreateTime,
            ai.create_time AS itemCreateTime
        FROM evaluation_apply_item ai
        INNER JOIN evaluation_apply a ON ai.apply_id = a.id
        INNER JOIN sys_user u ON a.student_id = u.id
        LEFT JOIN sys_college c ON u.college_id = c.id
        LEFT JOIN sys_class cl ON u.class_id = cl.id
        LEFT JOIN evaluation_rule_item ri ON ai.rule_item_id = ri.id
        <where>
            <if test='studentNo != null and studentNo != ""'>
                AND u.student_id LIKE CONCAT('%', #{studentNo}, '%')
            </if>
            <if test='periodId != null'>
                AND a.period_id = #{periodId}
            </if>
            <if test='applyStatus != null and applyStatus != ""'>
                AND a.status = #{applyStatus}
            </if>
            <if test='itemStatus != null and itemStatus != ""'>
                AND ai.status = #{itemStatus}
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
            @Param("periodId") Long periodId,
            @Param("applyStatus") String applyStatus,
            @Param("itemStatus") String itemStatus,
            @Param("collegeId") Long collegeId,
            @Param("classId") Long classId);

    @Update("UPDATE evaluation_apply_item SET status = #{status} WHERE id = #{applyItemId}")
    int updateApplyItemStatus(@Param("applyItemId") Long applyItemId, @Param("status") String status);

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

    @Select("SELECT COUNT(1) FROM evaluation_apply_item WHERE apply_id = #{applyId}")
    int countApplyItems(@Param("applyId") Long applyId);

    @Select("SELECT COUNT(1) FROM evaluation_apply_item WHERE apply_id = #{applyId} AND status = #{status}")
    int countApplyItemsByStatus(@Param("applyId") Long applyId, @Param("status") String status);

    @Update("UPDATE evaluation_apply SET status = #{status}, update_time = NOW() WHERE id = #{applyId}")
    void updateApplyStatus(@Param("applyId") Long applyId, @Param("status") String status);
}
