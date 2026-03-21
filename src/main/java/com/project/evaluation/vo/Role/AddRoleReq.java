package com.project.evaluation.vo.Role;

import lombok.Data;

/**
 * 添加角色请求体
 */
@Data
public class AddRoleReq {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态（1启用 0禁用）
     */
    private Integer status;
}