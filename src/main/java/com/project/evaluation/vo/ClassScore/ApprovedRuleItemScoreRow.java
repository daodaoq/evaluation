package com.project.evaluation.vo.ClassScore;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApprovedRuleItemScoreRow {
    private Long studentUserId;
    /** 申报项 id，用于计分时区分同细则多条通过记录 */
    private Long applyItemId;
    private Long ruleItemId;
    private String itemName;
    private String moduleCode;
    private String submoduleCode;
    private String level;
    private BigDecimal baseScore;
    private BigDecimal coeff;
    private String scoreMode;
    private String dedupeGroup;
    /** 审批写入的得分；0 表示按细则回算 */
    private BigDecimal persistedScore;
    private String sourceType;
    private Integer itemCategory;
}
