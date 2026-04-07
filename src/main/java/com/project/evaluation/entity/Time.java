package com.project.evaluation.entity;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * 综测周期及阶段时间配置，与表 evaluation_period 对应。
 */
@Data
public class Time {

    /** 综测周期主键 */
    private Integer id;

    /** 周期名称（展示用，如「2025-2026学年第二学期」） */
    private String periodName;

    /** 周期整体开始时间（字符串存储，与业务配置一致） */
    private String startTime;

    /** 周期整体结束时间（字符串存储） */
    private String endTime;

    /** 是否启用：1 启用，0 停用 */
    private Integer status;

    /** 是否归档锁定：1 已归档（仅可查不可改关键数据） */
    private Integer archived;

    /** 学生申报开放开始时间；为空时可回退用 startTime 解析 */
    private LocalDateTime applicationStartTime;
    /** 学生申报截止 */
    private LocalDateTime applicationEndTime;
    /** 教师审核截止 */
    private LocalDateTime reviewEndTime;
    /** 公示开始 */
    private LocalDateTime publicNoticeStart;
    /** 公示结束 */
    private LocalDateTime publicNoticeEnd;
    /** 异议提交截止；为空时业务上可与公示结束时间一致 */
    private LocalDateTime objectionEndTime;

    /** 记录创建时间 */
    private LocalDateTime createTime;
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;
}
