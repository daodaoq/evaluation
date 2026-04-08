package com.project.evaluation.controller;

import com.project.evaluation.entity.Result;
import com.project.evaluation.entity.SysPermission;
import com.project.evaluation.service.SysPermissionService;
import com.project.evaluation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统权限（表 sys_permission）：当前用户菜单与全量菜单均在此控制器下。
 */
@RestController
@RequestMapping("/sys-permission")
public class SysPermissionController {

    @Autowired
    private SysPermissionService sysPermissionService;

    @Autowired
    private UserService userService;

    /**
     * 当前登录用户在角色下拥有的权限行（扁平，含 parent_id），供侧栏与动态路由。
     */
    @GetMapping("/my")
    public Result<List<SysPermission>> getMyPermissions() {
        return userService.getUserPermissions();
    }

    /**
     * 全库启用中的权限（扁平），供角色分配等管理端场景。
     */
    @GetMapping("/full")
    @PreAuthorize("hasAuthority('sys:perm:menu')")
    public Result<List<SysPermission>> listFullMenu() {
        return Result.success(sysPermissionService.listAllEnabled());
    }

    /**
     * 菜单类权限（perm_code 含 :menu），用于角色分配「菜单」穿梭框。
     */
    @GetMapping("/menus")
    @PreAuthorize("hasAnyAuthority('sys:role:menu','sys:perm:menu')")
    public Result<List<SysPermission>> listMenusForAssign() {
        return Result.success(sysPermissionService.listMenuEnabled());
    }
}
