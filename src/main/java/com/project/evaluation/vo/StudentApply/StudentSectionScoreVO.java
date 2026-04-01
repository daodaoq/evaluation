package com.project.evaluation.vo.StudentApply;

import lombok.Data;

import java.math.BigDecimal;

/** 学生端：某综测大类下当前已得分（展示用） */
@Data
public class StudentSectionScoreVO {
    private String sectionCode;
    private String sectionTitle;
    private BigDecimal earnedScore;
}
