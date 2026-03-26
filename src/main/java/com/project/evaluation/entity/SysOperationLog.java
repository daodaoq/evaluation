package com.project.evaluation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统操作日志：sys_operation_log
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysOperationLog {
    private Long id;
    private Long userId;
    /** 关联 sys_user.student_id（查询联表填充） */
    private String studentId;
    /** 关联 sys_user.real_name（查询联表填充） */
    private String realName;
    private String operation;
    private String content;
    private String ipAddress;
    private LocalDateTime createTime;
}

