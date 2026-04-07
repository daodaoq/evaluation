package com.project.evaluation.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 班级，与表 sys_class 对应。
 */
@Data
public class Class {
    /** 主键 */
    private Integer id;

    /** 班级名称 */
    private String className;

    /** 所属学院 id */
    private Integer collegeId;

    /** 年级（如入学年份） */
    private Integer gradeYear;

    /** 记录创建时间 */
    private LocalDateTime createTime;
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;

}
