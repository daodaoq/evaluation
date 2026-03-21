package com.project.evaluation.service;

import com.project.evaluation.entity.Authority;
import com.project.evaluation.entity.Result;
import com.project.evaluation.vo.User.LoginReq;

import java.util.List;

public interface UserService {
    Result checkLogin(LoginReq loginReq);

    Result logout();

    Result<List<Authority>> getUserAuthority();
}
