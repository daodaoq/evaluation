package com.project.evaluation.service;

import com.project.evaluation.entity.SysPermission;

import java.util.List;

public interface SysPermissionService {

    /**
     * 数据库中全部启用状态的权限（扁平）
     */
    List<SysPermission> listAllEnabled();

    /**
     * 用于角色分配的菜单类权限（perm_code 含 :menu）
     */
    List<SysPermission> listMenuEnabled();
}
