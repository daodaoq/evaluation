package com.project.evaluation.vo.Time;

import lombok.Data;

/**
 * 添加周期请求体
 */
@Data
public class AddTimeReq {
    /**
     * 周期名称
     */
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
    private Integer status;

}
