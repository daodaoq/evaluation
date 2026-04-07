package com.project.evaluation.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 单次申报下的具体条目（选细则或自填项），与表 evaluation_apply_item 对应。
 */
@Data
public class EvaluationApplyItem {
    /** 主键 */
    private Long id;
    /** 所属申报单 id（evaluation_apply.id） */
    private Long applyId;
    /** 关联规则项 id；非细则项可为空 */
    private Long ruleItemId;
    /** 审核认定后的得分 */
    private BigDecimal score;
    /** 审核状态：如 PENDING/APPROVED/REJECTED */
    private String status;
    /** 来源类型：RULE 细则项 / CUSTOM 非细则自填 */
    private String sourceType;
    /** 非细则项时的项目名称 */
    private String customName;
    /** 申报备注 */
    private String remark;
}
