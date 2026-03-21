package com.project.evaluation.controller;

import com.project.evaluation.entity.Authority;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.UserService;
import com.project.evaluation.vo.User.LoginReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param loginReq
     * @return
     */
    @PostMapping("/login")
    @CrossOrigin
    public Result login(@RequestBody LoginReq loginReq) {
        return userService.checkLogin(loginReq);
    }

    /**
     * 用户登出
     * @return
     */
    @GetMapping("/logout")
    @CrossOrigin
    public Result logout() {
        return userService.logout();
    }

    @GetMapping("/userinfo")
    @CrossOrigin
    public String getUser() {
        return "userinfo";
    }

    /**
     * 查询用户权限
     * @return
     */
    @GetMapping("/authority")
    @CrossOrigin
    public Result<List<Authority>> getUserAuthority() {
        return userService.getUserAuthority();
    }
}
