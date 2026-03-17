package com.project.evaluation.vo.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginReq {

    /**
     * 用户名
     */
    private String studentId;

    /**
     * 密码
     */
    private String password;
}
