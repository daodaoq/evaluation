package com.project.evaluation.vo.StudentApply;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class StudentCategoryScoreOverviewVO {
    /** 仅含对学生可见的分类；父子关系已展开 */
    private List<StudentCategoryScoreNodeVO> categoryRoots = new ArrayList<>();
    /** 学业、未归入分类的任职分等 */
    private List<StudentScoreExtraRowVO> extraRows = new ArrayList<>();
    /** 分类树合计 + extraRows 之和 */
    private BigDecimal totalScore;
}
