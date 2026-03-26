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
    private String operation;
    private String content;
    private String ipAddress;
    private LocalDateTime createTime;
}

