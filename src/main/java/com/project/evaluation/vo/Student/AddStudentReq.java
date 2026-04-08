package com.project.evaluation.vo.Student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增学生账号（写入 sys_user，并绑定「学生」角色）
 */
@Data
public class AddStudentReq {

    /** 学号 / 登录账号 */
    @NotBlank(message = "学号不能为空")
    @Size(max = 64, message = "学号长度不能超过64")
    private String studentId;
    /** 初始密码 */
    @NotBlank(message = "密码不能为空")
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
    /** 默认 1 */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
