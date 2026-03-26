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
          id,
          user_id AS userId,
          operation,
          content,
          ip_address AS ipAddress,
          create_time AS createTime
        FROM sys_operation_log
        <where>
          <if test="userId != null">AND user_id = #{userId}</if>
          <if test="keyword != null and keyword != ''">
            AND (
              operation LIKE CONCAT('%', #{keyword}, '%')
              OR content LIKE CONCAT('%', #{keyword}, '%')
              OR ip_address LIKE CONCAT('%', #{keyword}, '%')
            )
          </if>
        </where>
        ORDER BY id DESC
        </script>
        """)
    java.util.List<com.project.evaluation.entity.SysOperationLog> selectOperationLogPage(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );
}

