package com.project.evaluation.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TeacherClassMapper {

    @Insert("INSERT INTO sys_teacher_class (teacher_user_id, class_id, create_time, update_time) VALUES (#{teacherUserId}, #{classId}, NOW(), NOW())")
    int insert(@Param("teacherUserId") Integer teacherUserId, @Param("classId") Integer classId);

    @Delete("DELETE FROM sys_teacher_class WHERE teacher_user_id = #{teacherUserId} AND class_id = #{classId}")
    int delete(@Param("teacherUserId") Integer teacherUserId, @Param("classId") Integer classId);

    @Delete("DELETE FROM sys_teacher_class WHERE teacher_user_id = #{teacherUserId}")
    int deleteByTeacherUserId(@Param("teacherUserId") Integer teacherUserId);

    @Select("SELECT class_id FROM sys_teacher_class WHERE teacher_user_id = #{teacherUserId} ORDER BY class_id")
    List<Integer> selectClassIdsByTeacherUserId(@Param("teacherUserId") Integer teacherUserId);
}
