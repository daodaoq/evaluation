package com.project.evaluation.vo.Student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新学生信息；password 为空则不修改密码
 */
@Data
public class UpdateStudentReq {

    @NotBlank(message = "学号不能为空")
    @Size(max = 64, message = "学号长度不能超过64")
    private String studentId;
    @Size(max = 128, message = "密码长度不能超过128")
    private String password;
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50")
    private String realName;
    @NotNull(message = "学院不能为空")
    @Positive(message = "学院ID必须为正数")
    private Integer collegeId;
    @NotNull(message = "班级不能为空")
    @Positive(message = "班级ID必须为正数")
    private Integer classId;
    @NotNull(message = "状态不能为空")
    private Integer status;
}
