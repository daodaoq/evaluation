package com.project.evaluation.vo.College;

import lombok.Data;

/**
 * 更新学院请求体
 */
@Data
public class UpdateCollegeReq {
    /**
     * 学院名称
     */
    private String collegeName;

    /**
     * 状态（1启用0停用）
     */
    private  Integer status;
}
