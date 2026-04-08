package com.project.evaluation.vo.Student;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DeleteStudentReq {
    @NotNull(message = "学生ID不能为空")
    @Positive(message = "学生ID必须为正数")
    private Integer id;
}
