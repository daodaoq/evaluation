package com.project.evaluation.vo.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * 批量为用户赋同一个角色
 */
@Data
public class BatchAssignRoleReq {
    @NotEmpty(message = "用户ID列表不能为空")
    private List<Integer> userIds;

    @NotNull(message = "角色ID不能为空")
    @Positive(message = "角色ID必须为正数")
    private Integer roleId;
}

