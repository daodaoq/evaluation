package com.project.evaluation.vo.Publicity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PublicitySaveReq {
    private Long id;
    private Long periodId;
    /** 空表示全院 */
    private Long classId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}
