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
import com.project.evaluation.vo.User.SetTeacherClassesReq;
import com.project.evaluation.vo.User.UpdateTeacherReq;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public Result<LoginResp> login(@Valid @RequestBody LoginReq loginReq) {
        return userService.checkLogin(loginReq);
    }

    /**
     * 用户登出
     * @return
     */
    @GetMapping("/logout")
    public Result<?> logout() {
        return userService.logout();
    }

    @GetMapping("/userinfo")
    public String getUser() {
        return "userinfo";
    }

    /**
     * 查询用户权限
     * @return
     */
    @GetMapping("/authority")
    public Result<List<Authority>> getUserAuthority() {
        return userService.getUserAuthority();
    }

    /**
     * 用户管理分页列表（不含密码）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:user:menu')")
    public Result<PageBean<LoginUserVO>> userList(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) List<Integer> statuses,
            @RequestParam(required = false) List<Integer> collegeIds) {
        return Result.success(userService.paginationQueryUsers(pageNum, pageSize, studentId, statuses, collegeIds));
    }

    /**
     * 新增教师（默认绑定教师角色）
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:user:menu')")
    public Result<?> addTeacher(@Valid @RequestBody AddTeacherReq req) {
        userService.addTeacher(req);
        return Result.success();
    }

    /**
     * 编辑教师
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:user:menu')")
    public Result<?> updateTeacher(@PathVariable Integer id, @Valid @RequestBody UpdateTeacherReq req) {
        userService.updateTeacher(id, req);
        return Result.success();
    }

    /**
     * 删除教师
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:user:menu')")
    public Result<?> deleteTeacher(@Valid @RequestBody DeleteTeacherReq req) {
        userService.deleteTeacher(req.getId());
        return Result.success();
    }

    /**
     * 教师管理页学院下拉
     */
    @GetMapping("/colleges")
    @PreAuthorize("hasAuthority('sys:user:menu')")
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
    public Result<?> assignRole(@Valid @RequestBody AssignRoleReq assignRoleReq){
        userService.assignRoles(assignRoleReq.getUserId(), assignRoleReq.getRoleIds());
        return Result.success();
    }

    /**
     * Excel 批量导入教师（必填列：工号、真实姓名、学院）
     */
    @PostMapping("/import-teacher-excel")
    @PreAuthorize("hasAuthority('sys:user:menu')")
    public Result<?> importTeacherExcel(@RequestParam("file") MultipartFile file) {
        int cnt = userService.importTeachersByExcel(file);
        return Result.success("导入成功，共 " + cnt + " 条");
    }

    /**
     * 批量给多个用户赋同一个角色（追加，不清空原有角色）
     */
    @PostMapping("/batch-assign-role")
    @PreAuthorize("hasAuthority('sys:user:menu')")
    public Result<?> batchAssignRole(@Valid @RequestBody BatchAssignRoleReq req) {
        int cnt = userService.batchAssignSameRole(req.getUserIds(), req.getRoleId());
        return Result.success("批量赋角色成功，更新 " + cnt + " 个用户");
    }

    /**
     * 获取用户的角色列表
     * @param userId
     * @return
     */
    @GetMapping("/roles/{userId}")
    @PreAuthorize("hasAuthority('sys:user:menu')")
    public Result<List<Integer>> getUserRoles(@PathVariable("userId") Integer userId){
        List<Integer> roleIds = userService.getUserRoles(userId);
        return Result.success(roleIds);
    }

    /**
     * 查询教师负责的班级 ID（仅管理员）
     */
    @GetMapping("/teacher/{teacherUserId}/classes")
    @PreAuthorize("hasAuthority('sys:user:menu')")
    public Result<List<Integer>> getTeacherClasses(@PathVariable Integer teacherUserId) {
        return Result.success(userService.getTeacherClassIds(teacherUserId));
    }

    /**
     * 全量设置教师负责班级（仅管理员；传空数组表示清空）
     */
    @PutMapping("/teacher/{teacherUserId}/classes")
    @PreAuthorize("hasAuthority('sys:user:menu')")
    public Result<?> setTeacherClasses(
            @PathVariable Integer teacherUserId,
            @Valid @RequestBody SetTeacherClassesReq req) {
        userService.setTeacherClasses(teacherUserId, req != null ? req.getClassIds() : null);
        return Result.success();
    }
}