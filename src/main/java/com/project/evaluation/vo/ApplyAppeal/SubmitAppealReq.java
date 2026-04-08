package com.project.evaluation.vo.ApplyAppeal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubmitAppealReq {
    @NotNull(message = "申报项ID不能为空")
    @Positive(message = "申报项ID必须为正数")
    private Long applyItemId;

    @NotBlank(message = "请填写申诉理由")
    @Size(max = 1000, message = "申诉理由请勿超过1000字")
    private String reason;
}
