package com.project.evaluation.vo.RuleCategory;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 添加规则分类请求体
 */
@Data
public class AddRuleCategoryReq {

    /**
     * 插入后回填（复制分类树时使用）
     */
    private Integer id;

    /**
     * 规则总览 id
     */
    @NotNull(message = "规则ID不能为空")
    @Positive(message = "规则ID必须为正数")
    private Integer ruleId;

    /**
     * 分类名
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 64, message = "分类名称长度不能超过64")
    private String categoryName;

    /**
     * 分类父级 id
     */
    private Integer parentId;

    private BigDecimal scoreCap;

    /** 默认 1 */
    private Integer studentVisible;

    private Integer sortOrder;

    /** 分类基础分，默认 0 */
    private BigDecimal categoryBaseScore;
}
