package com.project.evaluation.vo.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新角色请求体
 */
@Data
public class UpdateRoleReq {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50")
    private String roleName;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码长度不能超过50")
    private String roleCode;

    /**
     * 角色描述
     */
    @Size(max = 255, message = "角色描述长度不能超过255")
    private String description;

    /**
     * 状态（1启用 0禁用）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}