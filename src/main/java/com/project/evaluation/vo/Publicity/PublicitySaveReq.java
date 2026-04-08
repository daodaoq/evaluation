package com.project.evaluation.vo.Publicity;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Data
public class PublicitySaveReq {
    private Long id;
    @NotNull(message = "周期ID不能为空")
    @Positive(message = "周期ID必须为正数")
    private Long periodId;
    /** 空表示全院 */
    private Long classId;
    @NotNull(message = "公示开始时间不能为空")
    private LocalDateTime startTime;
    @NotNull(message = "公示结束时间不能为空")
    private LocalDateTime endTime;
    @NotBlank(message = "状态不能为空")
    private String status;
}
