package com.project.evaluation.vo.Student;

import lombok.Data;

/**
 * 更新学生信息；password 为空则不修改密码
 */
@Data
public class UpdateStudentReq {

    private String studentId;
    private String password;
    private String realName;
    private Integer collegeId;
    private Integer classId;
    private Integer status;
}
