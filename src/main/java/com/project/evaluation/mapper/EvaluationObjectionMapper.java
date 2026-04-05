package com.project.evaluation.mapper;

import com.project.evaluation.entity.EvaluationObjection;
import com.project.evaluation.vo.Objection.ObjectionRowVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EvaluationObjectionMapper {

    @Insert("""
            INSERT INTO evaluation_objection
            (period_id, student_user_id, class_id, content, status, create_time, update_time)
            VALUES (#{periodId}, #{studentUserId}, #{classId}, #{content}, 'PENDING', NOW(), NOW())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(EvaluationObjection row);

    @Select("""
            SELECT id, period_id AS periodId, student_user_id AS studentUserId, class_id AS classId,
                   content, status, handler_user_id AS handlerUserId, handler_remark AS handlerRemark,
                   create_time AS createTime, update_time AS updateTime
            FROM evaluation_objection WHERE id = #{id}
            """)
    EvaluationObjection selectById(@Param("id") Long id);

    @Update("""
            UPDATE evaluation_objection
            SET status = #{status}, handler_user_id = #{handlerUserId}, handler_remark = #{remark}, update_time = NOW()
            WHERE id = #{id} AND status = 'PENDING'
            """)
    int updateHandled(@Param("id") Long id, @Param("status") String status,
                      @Param("handlerUserId") Long handlerUserId, @Param("remark") String remark);

    @Select("""
            <script>
            SELECT
                o.id,
                o.period_id AS periodId,
                o.student_user_id AS studentUserId,
                u.student_id AS studentNo,
                u.real_name AS studentName,
                cl.class_name AS className,
                o.content,
                o.status,
                o.handler_remark AS handlerRemark,
                o.create_time AS createTime
            FROM evaluation_objection o
            INNER JOIN sys_user u ON o.student_user_id = u.id
            LEFT JOIN sys_class cl ON u.class_id = cl.id
            <where>
                <if test="periodIds != null and periodIds.size() &gt; 0">
                    AND o.period_id IN
                    <foreach collection="periodIds" item="pid" open="(" separator="," close=")">#{pid}</foreach>
                </if>
                <if test="statuses != null and statuses.size() &gt; 0">
                    AND o.status IN
                    <foreach collection="statuses" item="st" open="(" separator="," close=")">#{st}</foreach>
                </if>
                <if test="collegeId != null">AND u.college_id = #{collegeId}</if>
                <if test="classIds != null and classIds.size() &gt; 0">
                    AND u.class_id IN
                    <foreach collection="classIds" item="cid" open="(" separator="," close=")">#{cid}</foreach>
                </if>
                <if test="classId != null">AND u.class_id = #{classId}</if>
            </where>
            ORDER BY o.id DESC
            </script>
            """)
    List<ObjectionRowVO> pageRows(@Param("periodIds") List<Long> periodIds,
                                  @Param("statuses") List<String> statuses,
                                  @Param("collegeId") Long collegeId,
                                  @Param("classId") Long classId,
                                  @Param("classIds") List<Integer> classIds);
}
