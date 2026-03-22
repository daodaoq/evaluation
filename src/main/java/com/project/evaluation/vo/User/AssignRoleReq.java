package com.project.evaluation.vo.User;

import lombok.Data;

import java.util.List;

/**
 * 用户分配角色请求体
 */
@Data
public class AssignRoleReq {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 角色ID列表
     */
    private List<Integer> roleIds;
}
