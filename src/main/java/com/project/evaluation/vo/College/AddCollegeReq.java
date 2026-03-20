package com.project.evaluation.vo.College;

import lombok.Data;

/**
 * 添加学院请求体
 */
@Data
public class AddCollegeReq {
    /**
     * 学院名称
     */
    private String collegeName;

    /**
     * 状态（1启用0停用）
     */
    private Integer status;
}
