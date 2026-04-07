package com.project.evaluation.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 综测结果公示时段配置，与表 evaluation_publicity 对应。
 */
@Data
public class EvaluationPublicity {
    /** 主键 */
    private Long id;
    /** 综测周期 id */
    private Long periodId;
    /** 班级 id（公示范围） */
    private Long classId;
    /** 公示开始时间 */
    private LocalDateTime startTime;
    /** 公示结束时间 */
    private LocalDateTime endTime;
    /** 状态（如 DRAFT/ACTIVE/ENDED 等业务约定） */
    private String status;
    /** 记录创建时间 */
    private LocalDateTime createTime;
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;
}
