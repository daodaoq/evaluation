package com.project.evaluation.service;

import com.project.evaluation.entity.Result;
import com.project.evaluation.vo.User.LoginReq;

public interface UserService {
    Result checkLogin(LoginReq loginReq);

    Result logout();
}
