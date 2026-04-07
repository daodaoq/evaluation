package com.project.evaluation.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 智育（学业）成绩导入记录，与表 evaluation_student_academic_score 对应，用于综测学业模块计算。
 */
@Data
public class AcademicScore {
    /** 主键 */
    private Long id;
    /** 综测周期 id */
    private Long periodId;
    /** 学号 */
    private String studentNo;
    /** 班级名称（展示或匹配用） */
    private String className;
    /** 学生姓名 */
    private String studentName;
    /** 智育成绩分值（如平均学分绩点折算分等，以业务规则为准） */
    private BigDecimal intellectualScore;
    /** 记录创建时间 */
    private LocalDateTime createTime;
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;
}
