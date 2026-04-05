package com.project.evaluation.vo.StudentApply;

import lombok.Data;

import java.math.BigDecimal;

/** 未挂在规则分类树上的得分（如智育、未绑定「任职分」分类的任职分等） */
@Data
public class StudentScoreExtraRowVO {
    private String label;
    private BigDecimal score;
}
