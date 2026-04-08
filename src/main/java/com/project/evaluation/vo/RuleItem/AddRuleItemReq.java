package com.project.evaluation.vo.RuleItem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 添加规则项请求体
 */
@Data
public class AddRuleItemReq {

    /**
     * 规则总览 id
     */
    @NotNull(message = "规则ID不能为空")
    @Positive(message = "规则ID必须为正数")
    private Long ruleId;

    /**
     * 指标项名称
     */
    @NotBlank(message = "指标项名称不能为空")
    @Size(max = 100, message = "指标项名称长度不能超过100")
    private String itemName;

    /**
     * 指标项类型
     * 0-加分项
     * 1-减分项
     */
    @NotNull(message = "指标项类型不能为空")
    private Integer itemType;

    /**
     * 规则项分类
     */
    private Integer itemCategory;

    /**
     * 规则项级别（如：国家级 / 省级 / 校级 等）
     */
    private String level;

    /**
     * 基础分值
     */
    private Double baseScore;

    /**
     * 是否竞赛项
     * 0-非竞赛
     * 1-竞赛项
     */
    private Integer isCompetition;

    /**
     * 是否需要材料
     * 0-不需要
     * 1-需要
     */
    private Integer needMaterial;

    /**
     * 是否启用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 计分方式（可空，默认 ADD）
     */
    private String scoreMode;

    private String dedupeGroup;

    /**
     * 系数（可空，默认 1）
     */
    private BigDecimal coeff;

    private String moduleCode;

    private String submoduleCode;
}
