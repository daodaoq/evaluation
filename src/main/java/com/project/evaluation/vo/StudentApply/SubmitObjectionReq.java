package com.project.evaluation.vo.StudentApply;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubmitObjectionReq {
    @NotNull(message = "请选择综测周期")
    @Positive(message = "综测周期ID必须为正数")
    private Long periodId;

    @NotBlank(message = "请填写异议内容")
    @Size(max = 2000, message = "异议内容请勿超过2000字")
    private String content;
}
