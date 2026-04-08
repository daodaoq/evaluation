package com.project.evaluation.vo.Time;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 添加周期请求体
 */
@Data
public class AddTimeReq {
    /**
     * 插入成功后由 MyBatis 回填（仅输出，请求体可忽略）
     */
    private Integer id;

    /**
     * 周期名称
     */
    @NotBlank(message = "周期名称不能为空")
    @Size(max = 64, message = "周期名称长度不能超过64")
    private String periodName;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 状态
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    private Integer archived;
    private LocalDateTime applicationStartTime;
    private LocalDateTime applicationEndTime;
    private LocalDateTime reviewEndTime;
    private LocalDateTime publicNoticeStart;
    private LocalDateTime publicNoticeEnd;
    private LocalDateTime objectionEndTime;
}
