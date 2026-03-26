package com.project.evaluation.controller;

import com.project.evaluation.entity.Authority;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.UserService;
import com.project.evaluation.vo.User.AssignRoleReq;
import com.project.evaluation.vo.User.LoginReq;
import com.project.evaluation.vo.User.LoginResp;
import com.project.evaluation.vo.User.LoginUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public Result<LoginResp> login(@RequestBody LoginReq loginReq) {
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

    /**
     * 用户管理分页列表（不含密码）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:user:menu')")
    @CrossOrigin
    public Result<PageBean<LoginUserVO>> userList(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) Integer status) {
        return Result.success(userService.paginationQueryUsers(pageNum, pageSize, studentId, status));
    }

    /**
     * 为用户分配角色
     * @param assignRoleReq
     * @return
     */
    @PostMapping("/assign-role")
    @PreAuthorize("hasAuthority('sys:user:menu')")
    @CrossOrigin
    public Result assignRole(@RequestBody AssignRoleReq assignRoleReq){
        userService.assignRoles(assignRoleReq.getUserId(), assignRoleReq.getRoleIds());
        return Result.success();
    }

    /**
     * 获取用户的角色列表
     * @param userId
     * @return
     */
    @GetMapping("/roles/{userId}")
    @PreAuthorize("hasAuthority('sys:user:menu')")
    @CrossOrigin
    public Result<List<Integer>> getUserRoles(@PathVariable("userId") Integer userId){
        List<Integer> roleIds = userService.getUserRoles(userId);
        return Result.success(roleIds);
    }
}