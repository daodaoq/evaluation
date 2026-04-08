package com.project.evaluation.vo.RuleItem;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class CopyRuleItemsReq {
    @NotNull(message = "源周期ID不能为空")
    @Positive(message = "源周期ID必须为正数")
    private Integer sourcePeriodId;
    @NotNull(message = "目标周期ID不能为空")
    @Positive(message = "目标周期ID必须为正数")
    private Integer targetPeriodId;
    /**
     * true: 覆盖目标学期既有规则项
     */
    @NotNull(message = "覆盖参数不能为空")
    private Boolean overwrite;
}
