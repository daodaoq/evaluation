package com.project.evaluation.vo.Class;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 删除班级请求体
 */
@Data
public class DeleteClassReq {
    /**
     * 班级ID
     */
    @NotNull(message = "班级ID不能为空")
    @Positive(message = "班级ID必须为正数")
    private Integer id;
}
