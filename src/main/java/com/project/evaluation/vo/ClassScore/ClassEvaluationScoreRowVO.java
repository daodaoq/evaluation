package com.project.evaluation.vo.ClassScore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassEvaluationScoreRowVO {
    /** 综测周期（多周期合并列表时用于区分） */
    private Long periodId;
    private Long userId;
    private String studentNo;
    private String studentName;
    private Long classId;
    private String className;
    /** 管理端上传的智育分 */
    private BigDecimal intellectualScore;
    private BigDecimal moralScore;
    private BigDecimal academicScore;
    private BigDecimal qualityBodymindScore;
    private BigDecimal qualityArtScore;
    private BigDecimal qualityLaborScore;
    private BigDecimal qualityInnovationScore;
    private BigDecimal totalScore;
}
