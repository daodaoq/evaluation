package com.project.evaluation.entity;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * 综测周期类
 */
@Data
public class Time {

    /**
     * id
     */
    private Integer id;

    /**
     * 周期名称
     */
    private String periodName;

    /**
     * 周期开始时间
     */
    private String startTime;

    /**
     * 周期结束时间
     */
    private String endTime;

    /**
     * 是否启用
     */
    private Integer status;

    /** 1=归档锁定 */
    private Integer archived;

    /** 申报开放起（空则用 startTime 字符串对应时间） */
    private LocalDateTime applicationStartTime;
    private LocalDateTime applicationEndTime;
    /** 教师审核截止 */
    private LocalDateTime reviewEndTime;
    private LocalDateTime publicNoticeStart;
    private LocalDateTime publicNoticeEnd;
    /** 异议截止（空则同公示结束） */
    private LocalDateTime objectionEndTime;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
