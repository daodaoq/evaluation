package com.project.evaluation.vo.Role;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotNull(message = "角色ID不能为空")
    @Positive(message = "角色ID必须为正数")
    private Integer roleId;

    /**
     * 权限ID列表
     */
    @NotEmpty(message = "权限ID列表不能为空")
    private List<Integer> permIds;
}
