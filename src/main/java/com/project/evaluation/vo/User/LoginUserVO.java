package com.project.evaluation.vo.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 登录成功返回的用户信息（对应 sys_user，不含 password）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserVO {

    private Long userId;
    /** 登录账号（学号/工号），对应 student_id */
    private String userNo;
    /** 真实姓名，对应 real_name */
    private String userName;
    private Long collegeId;
    private Long classId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
