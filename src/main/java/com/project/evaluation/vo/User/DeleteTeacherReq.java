package com.project.evaluation.vo.User;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DeleteTeacherReq {
    @NotNull(message = "教师ID不能为空")
    @Positive(message = "教师ID必须为正数")
    private Integer id;
}

