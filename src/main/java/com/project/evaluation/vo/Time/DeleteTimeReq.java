package com.project.evaluation.vo.Time;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 删除周期请求体
 */
@Data
public class DeleteTimeReq {
    /**
     * 待删除的id
     */
    @NotNull(message = "周期ID不能为空")
    @Positive(message = "周期ID必须为正数")
    private Integer id;
}
