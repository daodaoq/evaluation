package com.project.evaluation.vo.SubmitTip;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
public class SubmitTipSaveReq {
    private Long id;
    @NotNull(message = "周期ID不能为空")
    @Positive(message = "周期ID必须为正数")
    private Long periodId;
    @NotBlank(message = "分段编码不能为空")
    @Size(max = 32, message = "分段编码长度不能超过32")
    private String sectionCode;
    @NotBlank(message = "标题不能为空")
    @Size(max = 128, message = "标题长度不能超过128")
    private String title;
    @NotBlank(message = "内容不能为空")
    @Size(max = 2000, message = "内容长度不能超过2000")
    private String content;
    private Integer sortOrder;
    private Integer status;
}
