package com.project.evaluation.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Class {
    /**
     * 班级ID
     */
    private Integer id;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 学院ID（逻辑外键）
     */
    private Integer collegeId;

    /**
     * 年级
     */
    private Integer gradeYear;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
