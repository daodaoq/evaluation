package com.project.evaluation.vo.RuleCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新规则分类请求体
 */
@Data
public class UpdateRuleCategoryReq {

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
    @Size(max = 100, message = "分类名称长度不能超过100")
    private String categoryName;

    /**
     * 分类父级 id
     */
    @NotNull(message = "父级分类ID不能为空")
    private Integer parentId;

    private BigDecimal scoreCap;

    @NotNull(message = "学生可见性不能为空")
    private Integer studentVisible;

    private Integer sortOrder;

    private BigDecimal categoryBaseScore;
}
