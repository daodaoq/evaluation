package com.project.evaluation.vo.Class;

import lombok.Data;

/**
 * 更新班级请求体
 */
@Data
public class UpdateClassReq {
    /**
     * 班级名称
     */
    private String className;

    /**
     * 学院ID
     */
    private Integer collegeId;

    /**
     * 年级
     */
    private Integer gradeYear;
}
