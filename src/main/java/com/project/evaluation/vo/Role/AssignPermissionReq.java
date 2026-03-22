package com.project.evaluation.vo.Role;

import lombok.Data;

import java.util.List;

/**
 * 角色分配权限请求体
 */
@Data
public class AssignPermissionReq {

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 权限ID列表
     */
    private List<Integer> permIds;
}
