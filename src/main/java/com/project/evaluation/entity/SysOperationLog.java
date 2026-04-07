package com.project.evaluation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 管理端操作审计日志，与表 sys_operation_log 对应。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysOperationLog {
    /** 主键 */
    private Long id;
    /** 操作人用户 id（sys_user.id） */
    private Long userId;
    /** 关联学号（查询时联表填充，非表主存字段时可空） */
    private String studentId;
    /** 操作人真实姓名（查询时联表填充） */
    private String realName;
    /** 操作摘要（如请求方法+路径或业务动作名） */
    private String operation;
    /** 操作详情（如请求体摘要、关键参数） */
    private String content;
    /** 客户端 IP */
    private String ipAddress;
    /** 记录时间 */
    private LocalDateTime createTime;
}
