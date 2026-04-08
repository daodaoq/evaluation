package com.project.evaluation.vo.RuleCategory;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class CopyRuleCategoriesReq {
    @NotNull(message = "源周期ID不能为空")
    @Positive(message = "源周期ID必须为正数")
    private Integer sourcePeriodId;
    @NotNull(message = "目标周期ID不能为空")
    @Positive(message = "目标周期ID必须为正数")
    private Integer targetPeriodId;
    /**
     * true：删除目标规则下已有分类后再复制
     */
    @NotNull(message = "覆盖参数不能为空")
    private Boolean overwrite;
}
