package com.project.evaluation.vo.StudentApply;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** 学生端：可见分类树 + 挂载细则项 */
@Data
public class StudentRuleCategoryTreeNodeVO {
    private Integer id;
    private Integer parentId;
    private String categoryName;
    private Integer sortOrder;
    /** 后台配置的上限，仅展示；null 表示不限制 */
    private BigDecimal scoreCap;
    private List<StudentRuleCategoryTreeNodeVO> children = new ArrayList<>();
    private List<RuleItemSimpleVO> items = new ArrayList<>();
}
