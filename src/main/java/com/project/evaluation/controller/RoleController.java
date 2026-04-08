package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.entity.Role;
import com.project.evaluation.exception.BizException;
import com.project.evaluation.exception.ErrorCode;
import com.project.evaluation.service.RoleService;
import com.project.evaluation.vo.Role.AddRoleReq;
import com.project.evaluation.vo.Role.DeleteRoleReq;
import com.project.evaluation.vo.Role.UpdateRoleReq;
import com.project.evaluation.vo.Role.AssignPermissionReq;
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

import java.util.List;

@RestController
@RequestMapping("/sys-role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    /**
     * 添加角色
     * 
     * @param addRoleReq
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:role:menu')")
    public Result<?> addRole(@Valid @RequestBody AddRoleReq addRoleReq) {
        Role role = roleService.findRoleByName(addRoleReq.getRoleName());
        if (role == null) {
            roleService.addRole(addRoleReq);
            return Result.success();
        }
        throw new BizException(ErrorCode.BIZ_CONFLICT, "角色已存在");
    }

    /**
     * 删除角色
     * 
     * @param deleteRoleReq
     * @return
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:role:menu')")
    public Result<?> deleteRole(@Valid @RequestBody DeleteRoleReq deleteRoleReq) {
        roleService.deleteRole(deleteRoleReq.getId());
        return Result.success();
    }

    /**
     * 更新角色信息
     * 
     * @param updateRoleReq
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:role:menu')")
    public Result<?> updateRole(@Valid @RequestBody UpdateRoleReq updateRoleReq, @PathVariable("id") Integer id) {
        Role role = roleService.findRoleById(id);
        if (role != null) {
            roleService.updateRole(id, updateRoleReq);
            return Result.success();
        }
        throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "角色不存在");
    }

    /**
     * 批量获取角色列表
     * 
     * @return
     */
    @GetMapping
    @PreAuthorize("hasAuthority('sys:role:menu')")
    public Result<List<Role>> roleList() {
        List<Role> roles = roleService.roleList();
        return Result.success(roles);
    }

    /**
     * 查询单个角色详细信息
     * 
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:role:menu')")
    public Result<Role> findRoleById(@PathVariable("id") Integer id) {
        Role role = roleService.findRoleById(id);
        return Result.success(role);
    }

    /**
     * 分页条件查询角色
     * 
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:role:menu')")
    public Result<PageBean<Role>> paginationQuery(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) List<Integer> statuses) {
        PageBean<Role> pb = roleService.paginationQuery(pageNum, pageSize, statuses);
        return Result.success(pb);
    }

    /**
     * 为角色分配权限
     * 
     * @param assignPermissionReq
     * @return
     */
    @PostMapping("/assign-permission")
    @PreAuthorize("hasAuthority('sys:role:menu')")
    public Result<?> assignPermission(
            @Valid @RequestBody AssignPermissionReq assignPermissionReq) {
        roleService.assignPermissions(assignPermissionReq.getRoleId(), assignPermissionReq.getPermIds());
        return Result.success();
    }

    /**
     * 获取角色的权限列表
     * 
     * @param roleId
     * @return
     */
    @GetMapping("/permissions/{roleId}")
    @PreAuthorize("hasAuthority('sys:role:menu')")
    public Result<List<Integer>> getRolePermissions(@PathVariable("roleId") Integer roleId) {
        List<Integer> permIds = roleService.getRolePermissions(roleId);
        return Result.success(permIds);
    }
}