package com.project.evaluation.vo.Time;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 更新周期请求体
 */
@Data
public class UpdateTimeReq {

    /**
     * 周其名称
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
     * 状态
     */
    private Integer status;

    private Integer archived;
    private LocalDateTime applicationStartTime;
    private LocalDateTime applicationEndTime;
    private LocalDateTime reviewEndTime;
    private LocalDateTime publicNoticeStart;
    private LocalDateTime publicNoticeEnd;
    private LocalDateTime objectionEndTime;
}
