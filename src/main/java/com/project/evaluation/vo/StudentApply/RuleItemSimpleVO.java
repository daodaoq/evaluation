package com.project.evaluation.vo.StudentApply;

import lombok.Data;

@Data
public class RuleItemSimpleVO {
    private Long id;
    private Long ruleId;
    private String itemName;
    private Integer needMaterial;
    private Double baseScore;
    private Integer itemType;
    private Integer itemCategory;
    private String level;
    /** ADD / SUB / MAX_ONLY */
    private String scoreMode;
    /** MORAL / ACADEMIC / QUALITY */
    private String moduleCode;
    private Double coeff;
}
