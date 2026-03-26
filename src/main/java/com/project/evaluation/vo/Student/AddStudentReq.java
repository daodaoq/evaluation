package com.project.evaluation.vo.Student;

import lombok.Data;

/**
 * 新增学生账号（写入 sys_user，并绑定「学生」角色）
 */
@Data
public class AddStudentReq {

    /** 学号 / 登录账号 */
    private String studentId;
    /** 初始密码 */
    private String password;
    private String realName;
    private Integer collegeId;
    private Integer classId;
    /** 默认 1 */
    private Integer status;
}
