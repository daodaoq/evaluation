package com.project.evaluation.vo.ApplyAppeal;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
public class HandleAppealReq {
    @NotNull(message = "申诉ID不能为空")
    @Positive(message = "申诉ID必须为正数")
    private Long appealId;
    /** ACCEPTED：通过申诉，申报项退回待审；REJECTED：驳回申诉 */
    @NotBlank(message = "处理结果不能为空")
    private String decision;
    @Size(max = 500, message = "处理备注长度不能超过500")
    private String handlerRemark;
}
