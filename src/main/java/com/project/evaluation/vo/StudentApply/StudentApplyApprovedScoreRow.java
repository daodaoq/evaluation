package com.project.evaluation.vo.StudentApply;

import lombok.Data;

import java.math.BigDecimal;

/** 单条已通过申报项得分（用于按模块汇总） */
@Data
public class StudentApplyApprovedScoreRow {
    private BigDecimal score;
    private String sourceType;
    private String moduleCode;
    private String submoduleCode;
    /** 以下字段来自细则，用于库内 score 仍为 0 时按规则回算（历史通过记录） */
    private BigDecimal baseScore;
    private BigDecimal coeff;
    private String scoreMode;
}
