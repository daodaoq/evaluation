package com.project.evaluation.controller;

import com.project.evaluation.entity.Authority;
import com.project.evaluation.entity.College;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.UserService;
import com.project.evaluation.vo.User.AddTeacherReq;
import com.project.evaluation.vo.User.AssignRoleReq;
import com.project.evaluation.vo.User.BatchAssignRoleReq;
import com.project.evaluation.vo.User.DeleteTeacherReq;
import com.project.evaluation.vo.User.LoginReq;
import com.project.evaluation.vo.User.LoginResp;
import com.project.evaluation.vo.User.LoginUserVO;
import com.project.evaluation.vo.User.UpdateTeacherReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * 新增教师（默认绑定教师角色）
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:user:menu')")
    @CrossOrigin
    public Result<?> addTeacher(@RequestBody AddTeacherReq req) {
        try {
            userService.addTeacher(req);
            return Result.success();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 编辑教师
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:user:menu')")
    @CrossOrigin
    public Result<?> updateTeacher(@PathVariable Integer id, @RequestBody UpdateTeacherReq req) {
        try {
            userService.updateTeacher(id, req);
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除教师
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:user:menu')")
    @CrossOrigin
    public Result<?> deleteTeacher(@RequestBody DeleteTeacherReq req) {
        try {
            userService.deleteTeacher(req.getId());
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 教师管理页学院下拉
     */
    @GetMapping("/colleges")
    @PreAuthorize("hasAuthority('sys:user:menu')")
    @CrossOrigin
    public Result<List<College>> colleges() {
        return Result.success(userService.listColleges());
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
     * Excel 批量导入教师（必填列：工号、真实姓名、学院）
     */
    @PostMapping("/import-teacher-excel")
    @PreAuthorize("hasAuthority('sys:user:menu')")
    @CrossOrigin
    public Result<?> importTeacherExcel(@RequestParam("file") MultipartFile file) {
        try {
            int cnt = userService.importTeachersByExcel(file);
            return Result.success("导入成功，共 " + cnt + " 条");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量给多个用户赋同一个角色（追加，不清空原有角色）
     */
    @PostMapping("/batch-assign-role")
    @PreAuthorize("hasAuthority('sys:user:menu')")
    @CrossOrigin
    public Result<?> batchAssignRole(@RequestBody BatchAssignRoleReq req) {
        try {
            int cnt = userService.batchAssignSameRole(req.getUserIds(), req.getRoleId());
            return Result.success("批量赋角色成功，更新 " + cnt + " 个用户");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
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