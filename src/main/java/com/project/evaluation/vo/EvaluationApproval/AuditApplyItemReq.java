package com.project.evaluation.vo.EvaluationApproval;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuditApplyItemReq {
    @NotNull(message = "申报项ID不能为空")
    @Positive(message = "申报项ID必须为正数")
    private Long applyItemId;

    @Size(max = 500, message = "审核备注请勿超过500字")
    private String remark;
}
