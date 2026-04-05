package com.project.evaluation.vo.StudentApply;

import lombok.Data;

import java.math.BigDecimal;

/** 细则计分元数据（学生提交时按分数比例写入申报项 score） */
@Data
public class RuleItemScoreMeta {
    private BigDecimal baseScore;
    private BigDecimal coeff;
    private String scoreMode;
}
