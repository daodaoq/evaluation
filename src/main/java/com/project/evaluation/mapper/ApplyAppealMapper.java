package com.project.evaluation.mapper;

import com.project.evaluation.entity.EvaluationApplyItemAppeal;
import com.project.evaluation.vo.ApplyAppeal.ApplyAppealRowVO;
import com.project.evaluation.vo.ApplyAppeal.ApplyItemOwnerRow;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ApplyAppealMapper {

    @Select("""
        SELECT a.student_id AS studentId, ai.status AS itemStatus
        FROM evaluation_apply_item ai
        INNER JOIN evaluation_apply a ON ai.apply_id = a.id
        WHERE ai.id = #{applyItemId}
        """)
    ApplyItemOwnerRow selectItemOwner(@Param("applyItemId") Long applyItemId);

    @Select("""
        SELECT COUNT(1) FROM evaluation_apply_item_appeal
        WHERE apply_item_id = #{applyItemId} AND status = 'PENDING'
        """)
    int countPendingByApplyItemId(@Param("applyItemId") Long applyItemId);

    @Insert("""
        INSERT INTO evaluation_apply_item_appeal
        (apply_item_id, student_id, reason, status, create_time, update_time)
        VALUES (#{applyItemId}, #{studentId}, #{reason}, #{status}, NOW(), NOW())
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(EvaluationApplyItemAppeal row);

    @Select("""
        SELECT id, apply_item_id AS applyItemId, student_id AS studentId, status
        FROM evaluation_apply_item_appeal WHERE id = #{id}
        """)
    EvaluationApplyItemAppeal selectById(@Param("id") Long id);

    @Update("""
        UPDATE evaluation_apply_item_appeal
        SET status = #{status}, handler_id = #{handlerId}, handler_remark = #{handlerRemark}, update_time = NOW()
        WHERE id = #{id} AND status = 'PENDING'
        """)
    int updateHandled(@Param("id") Long id,
                      @Param("status") String status,
                      @Param("handlerId") Long handlerId,
                      @Param("handlerRemark") String handlerRemark);

    @Select("""
        <script>
        SELECT
            ap.id AS appealId,
            ai.id AS applyItemId,
            a.id AS applyId,
            u.student_id AS studentNo,
            u.real_name AS studentName,
            c.college_name AS collegeName,
            cl.class_name AS className,
            a.period_id AS periodId,
            a.status AS applyStatus,
            ai.status AS itemStatus,
            ri.item_name AS ruleItemName,
            ai.custom_name AS customName,
            ap.reason AS appealReason,
            ap.status AS appealStatus,
            ap.create_time AS appealCreateTime,
            ap.handler_remark AS handlerRemark
        FROM evaluation_apply_item_appeal ap
        INNER JOIN evaluation_apply_item ai ON ap.apply_item_id = ai.id
        INNER JOIN evaluation_apply a ON ai.apply_id = a.id
        INNER JOIN sys_user u ON a.student_id = u.id
        LEFT JOIN sys_college c ON u.college_id = c.id
        LEFT JOIN sys_class cl ON u.class_id = cl.id
        LEFT JOIN evaluation_rule_item ri ON ai.rule_item_id = ri.id
        <where>
            <if test="studentNo != null and studentNo != ''">
                AND u.student_id LIKE CONCAT('%', #{studentNo}, '%')
            </if>
            <if test="periodId != null">
                AND a.period_id = #{periodId}
            </if>
            <if test="appealStatus != null and appealStatus != ''">
                AND ap.status = #{appealStatus}
            </if>
            <if test="collegeId != null">
                AND u.college_id = #{collegeId}
            </if>
            <if test="classId != null">
                AND u.class_id = #{classId}
            </if>
            <if test="classIds != null and classIds.size() &gt; 0">
                AND u.class_id IN
                <foreach collection="classIds" item="cid" open="(" separator="," close=")">#{cid}</foreach>
            </if>
        </where>
        ORDER BY ap.id DESC
        </script>
        """)
    List<ApplyAppealRowVO> pageAppeals(@Param("studentNo") String studentNo,
                                       @Param("periodId") Long periodId,
                                       @Param("appealStatus") String appealStatus,
                                       @Param("collegeId") Long collegeId,
                                       @Param("classId") Long classId,
                                       @Param("classIds") List<Integer> classIds);
}
