package com.project.evaluation.vo.Objection;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
public class HandleObjectionReq {
    @NotNull(message = "异议ID不能为空")
    @Positive(message = "异议ID必须为正数")
    private Long objectionId;
    /** HANDLED=已处理并采纳说明 / REJECTED=驳回异议 */
    @NotBlank(message = "处理结果不能为空")
    private String decision;
    @Size(max = 500, message = "处理备注长度不能超过500")
    private String handlerRemark;
}
