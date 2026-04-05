package com.project.evaluation.vo.StudentApply;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生端：单个规则分类上的得分（基础分 + 本分类直接挂载的细则得分，含上限；totalScore 含子分类汇总）。
 */
@Data
public class StudentCategoryScoreNodeVO {
    private Integer categoryId;
    private Integer parentId;
    private String categoryName;
    private Integer sortOrder;

    /** 分类配置的基础分 */
    private BigDecimal categoryBaseScore;

    /** 配置的上限（展示用）；null 表示不限制 */
    private BigDecimal scoreCap;

    /**
     * 本分类下细则（item_category 指向本节点）经去重后的净得分：加分（已过上限截断）+ 扣分行。
     */
    private BigDecimal itemScore;

    /** 基础分 + itemScore（不含子分类） */
    private BigDecimal subtotal;

    /** subtotal + 各子节点 totalScore */
    private BigDecimal totalScore;

    private List<StudentCategoryScoreNodeVO> children = new ArrayList<>();
}
