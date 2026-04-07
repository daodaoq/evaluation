package com.project.evaluation.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生对综测结果公示的异议记录，与表 evaluation_objection 对应。
 */
@Data
public class EvaluationObjection {
    /** 主键 */
    private Long id;
    /** 综测周期 id */
    private Long periodId;
    /** 提出异议的学生用户 id（sys_user.id） */
    private Long studentUserId;
    /** 学生所属班级 id */
    private Integer classId;
    /** 异议内容 */
    private String content;
    /** 处理状态（如 PENDING/PROCESSING/CLOSED 等业务约定） */
    private String status;
    /** 处理人用户 id（sys_user.id） */
    private Long handlerUserId;
    /** 处理说明或备注 */
    private String handlerRemark;
    /** 记录创建时间 */
    private LocalDateTime createTime;
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;
}
