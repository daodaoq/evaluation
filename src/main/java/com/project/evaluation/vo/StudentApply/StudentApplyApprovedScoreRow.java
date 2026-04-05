package com.project.evaluation.vo.StudentApply;

import lombok.Data;

import java.math.BigDecimal;

/** 单条已通过申报项得分（用于按模块汇总） */
@Data
public class StudentApplyApprovedScoreRow {
    /** 申报项主键，用于计分时区分同细则多条通过记录（不参与同类取最高合并） */
    private Long applyItemId;
    private Long ruleItemId;
    private String itemName;
    private String level;
    private String dedupeGroup;

    private BigDecimal score;
    private String sourceType;
    /** 非细则项名称；任职分时用于归入德育汇总 */
    private String customName;
    private String moduleCode;
    private String submoduleCode;
    private BigDecimal baseScore;
    private BigDecimal coeff;
    private String scoreMode;
    /** 细则项所属分类 */
    private Integer itemCategory;
}
