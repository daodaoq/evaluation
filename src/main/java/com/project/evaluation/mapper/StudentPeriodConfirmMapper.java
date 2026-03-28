package com.project.evaluation.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentPeriodConfirmMapper {

    @Select("""
            SELECT COUNT(1) FROM evaluation_student_period_confirm
            WHERE student_user_id = #{studentUserId} AND period_id = #{periodId}
            """)
    int countByStudentAndPeriod(@Param("studentUserId") Long studentUserId, @Param("periodId") Long periodId);

    default boolean exists(Long studentUserId, Long periodId) {
        return studentUserId != null && periodId != null && countByStudentAndPeriod(studentUserId, periodId) > 0;
    }

    @Insert("""
            INSERT INTO evaluation_student_period_confirm (student_user_id, period_id, create_time)
            VALUES (#{studentUserId}, #{periodId}, NOW())
            """)
    int insert(@Param("studentUserId") Long studentUserId, @Param("periodId") Long periodId);
}
