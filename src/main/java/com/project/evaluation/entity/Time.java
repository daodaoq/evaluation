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
    private  Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
