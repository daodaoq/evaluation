package com.project.evaluation.vo.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录接口响应体，与前端约定字段 token + user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResp {

    private String token;
    /** token 过期时间戳（毫秒） */
    private Long expireAt;
    private LoginUserVO user;
}
