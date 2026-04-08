package com.project.evaluation.vo.Role;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 删除角色请求体
 */
@Data
public class DeleteRoleReq {

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    @Positive(message = "角色ID必须为正数")
    private Integer id;
}