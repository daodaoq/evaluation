package com.project.evaluation.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysOperationLogMapper {

    @Insert("INSERT INTO `sys_operation_log` (user_id, operation, content, ip_address, create_time) " +
            "VALUES (#{userId}, #{operation}, #{content}, #{ipAddress}, NOW())")
    void insert(
            @Param("userId") Long userId,
            @Param("operation") String operation,
            @Param("content") String content,
            @Param("ipAddress") String ipAddress
    );

    @Select("""
        <script>
        SELECT
          l.id,
          l.user_id AS userId,
          u.student_id AS studentId,
          u.real_name AS realName,
          l.operation,
          l.content,
          l.ip_address AS ipAddress,
          l.create_time AS createTime
        FROM sys_operation_log l
        LEFT JOIN sys_user u ON l.user_id = u.id
        <where>
          <if test="userId != null">AND l.user_id = #{userId}</if>
          <if test="keyword != null and keyword != ''">
            AND (
              l.operation LIKE CONCAT('%', #{keyword}, '%')
              OR l.content LIKE CONCAT('%', #{keyword}, '%')
              OR l.ip_address LIKE CONCAT('%', #{keyword}, '%')
              OR u.student_id LIKE CONCAT('%', #{keyword}, '%')
              OR u.real_name LIKE CONCAT('%', #{keyword}, '%')
            )
          </if>
        </where>
        ORDER BY l.id DESC
        </script>
        """)
    java.util.List<com.project.evaluation.entity.SysOperationLog> selectOperationLogPage(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );
}

