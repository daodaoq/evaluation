package com.project.evaluation.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 学生一次综测申报（按周期），与表 evaluation_apply 对应。
 */
@Data
public class EvaluationApply {
    /** 主键 */
    private Long id;
    /** 学生用户 id（sys_user.id） */
    private Long studentId;
    /** 综测周期 id */
    private Long periodId;
    /** 申报单状态：如 DRAFT/SUBMITTED/APPROVED/REJECTED */
    private String status;
    /** 申报汇总总分（业务计算后写入） */
    private BigDecimal totalScore;
}
