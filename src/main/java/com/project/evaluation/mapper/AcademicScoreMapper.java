package com.project.evaluation.mapper;

import com.project.evaluation.entity.AcademicScore;
import com.project.evaluation.vo.AcademicScore.MyAcademicScoreVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AcademicScoreMapper {

    @Select("""
        <script>
        SELECT *
        FROM evaluation_student_academic_score
        <where>
            <if test="periodIds != null and periodIds.size() &gt; 0">
                AND period_id IN
                <foreach collection="periodIds" item="pid" open="(" separator="," close=")">#{pid}</foreach>
            </if>
            <if test="studentNo != null and studentNo != ''"> AND student_no LIKE CONCAT('%', #{studentNo}, '%') </if>
            <if test="classNames != null and classNames.size() &gt; 0">
                AND class_name IN
                <foreach collection="classNames" item="cn" open="(" separator="," close=")">#{cn}</foreach>
            </if>
            <if test="studentName != null and studentName != ''"> AND student_name LIKE CONCAT('%', #{studentName}, '%') </if>
        </where>
        ORDER BY id DESC
        </script>
        """)
    List<AcademicScore> pageQuery(@Param("periodIds") List<Long> periodIds,
                                  @Param("studentNo") String studentNo,
                                  @Param("classNames") List<String> classNames,
                                  @Param("studentName") String studentName);

    @Insert("""
        INSERT INTO evaluation_student_academic_score
        (period_id, student_no, class_name, student_name, intellectual_score, create_time, update_time)
        VALUES (#{periodId}, #{studentNo}, #{className}, #{studentName}, #{intellectualScore}, NOW(), NOW())
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(AcademicScore score);

    @Update("""
        UPDATE evaluation_student_academic_score
        SET period_id = #{periodId},
            student_no = #{studentNo},
            class_name = #{className},
            student_name = #{studentName},
            intellectual_score = #{intellectualScore},
            update_time = NOW()
        WHERE id = #{id}
        """)
    int updateById(AcademicScore score);

    @Delete("DELETE FROM evaluation_student_academic_score WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT * FROM evaluation_student_academic_score WHERE id = #{id} LIMIT 1")
    AcademicScore findById(@Param("id") Long id);

    @Select("""
        SELECT * FROM evaluation_student_academic_score
        WHERE period_id = #{periodId} AND student_no = #{studentNo}
        LIMIT 1
        """)
    AcademicScore findByPeriodAndStudentNo(@Param("periodId") Long periodId, @Param("studentNo") String studentNo);

    @Select("SELECT COUNT(1) FROM evaluation_period WHERE id = #{periodId}")
    int countPeriod(@Param("periodId") Long periodId);

    @Select("SELECT COUNT(1) FROM sys_user WHERE student_id = #{studentNo}")
    int countStudentNo(@Param("studentNo") String studentNo);

    @Select("""
        SELECT
          eas.period_id AS periodId,
          eas.student_no AS studentNo,
          eas.class_name AS className,
          eas.student_name AS studentName,
          eas.intellectual_score AS intellectualScore
        FROM evaluation_student_academic_score eas
        INNER JOIN sys_user su ON su.student_id = eas.student_no
        WHERE su.id = #{studentUserId} AND eas.period_id = #{periodId}
        LIMIT 1
        """)
    MyAcademicScoreVO findMyScore(@Param("studentUserId") Long studentUserId, @Param("periodId") Long periodId);

    @Select("""
        SELECT
          su.student_id AS studentNo,
          su.real_name AS studentName,
          COALESCE(sc.class_name, '') AS className
        FROM sys_user su
        LEFT JOIN sys_class sc ON sc.id = su.class_id
        WHERE su.student_id = #{studentNo}
        LIMIT 1
        """)
    MyAcademicScoreVO findStudentSnapshotByStudentNo(@Param("studentNo") String studentNo);
}
