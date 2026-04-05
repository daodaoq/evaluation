package com.project.evaluation.vo.AcademicScore;

import lombok.Data;

/**
 * 智育管理页班级下拉：展示学院 + 班级，存储仍使用班级名称（与库表一致）。
 */
@Data
public class ClassOptionVO {
    private Integer id;
    private String className;
    private Integer collegeId;
    private String collegeName;
    private Integer gradeYear;
}
