package com.project.evaluation.vo.User;

import lombok.Data;

@Data
public class AddTeacherReq {
    private String teacherNo;
    private String password;
    private String realName;
    private Integer collegeId;
    private Integer status;
}

