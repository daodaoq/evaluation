package com.project.evaluation.mapper;

import com.project.evaluation.entity.EvaluationSubmitTip;
import com.project.evaluation.vo.SubmitTip.SubmitTipSaveReq;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EvaluationSubmitTipMapper {

    @Insert("""
            INSERT INTO evaluation_student_submit_tip
            (period_id, section_code, title, content, sort_order, status, operator_user_id, create_time, update_time)
            VALUES (#{periodId}, #{sectionCode}, #{title}, #{content}, #{sortOrder}, #{status}, #{operatorUserId}, NOW(), NOW())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(EvaluationSubmitTip row);

    @Update("""
            UPDATE evaluation_student_submit_tip SET
            period_id = #{periodId},
            section_code = #{sectionCode},
            title = #{title},
            content = #{content},
            sort_order = #{sortOrder},
            status = #{status},
            operator_user_id = #{operatorUserId},
            update_time = NOW()
            WHERE id = #{id}
            """)
    int updateById(@Param("id") Long id, @Param("periodId") Long periodId, @Param("sectionCode") String sectionCode,
                   @Param("title") String title, @Param("content") String content, @Param("sortOrder") Integer sortOrder,
                   @Param("status") Integer status, @Param("operatorUserId") Integer operatorUserId);

    @Delete("DELETE FROM evaluation_student_submit_tip WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT * FROM evaluation_student_submit_tip WHERE id = #{id}")
    EvaluationSubmitTip selectById(@Param("id") Long id);

    @Select("""
            <script>
            SELECT * FROM evaluation_student_submit_tip
            <where>
              <if test="periodIds != null and periodIds.size() &gt; 0">
                AND period_id IN
                <foreach collection="periodIds" item="pid" open="(" separator="," close=")">#{pid}</foreach>
              </if>
              <if test="sectionCodes != null and sectionCodes.size() &gt; 0">
                AND section_code IN
                <foreach collection="sectionCodes" item="sc" open="(" separator="," close=")">#{sc}</foreach>
              </if>
            </where>
            ORDER BY period_id DESC, section_code ASC, sort_order ASC, id DESC
            </script>
            """)
    List<EvaluationSubmitTip> listForManage(@Param("periodIds") List<Long> periodIds,
                                            @Param("sectionCodes") List<String> sectionCodes);

    @Select("""
            <script>
            SELECT * FROM evaluation_student_submit_tip
            WHERE period_id = #{periodId}
              AND status = 1
              <if test="sectionCode != null and sectionCode != ''">AND section_code = #{sectionCode}</if>
            ORDER BY section_code ASC, sort_order ASC, id DESC
            </script>
            """)
    List<EvaluationSubmitTip> listForStudent(@Param("periodId") Long periodId, @Param("sectionCode") String sectionCode);
}
