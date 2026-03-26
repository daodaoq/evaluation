package com.project.evaluation.vo.User;

import lombok.Data;

import java.util.List;

/**
 * 批量为用户赋同一个角色
 */
@Data
public class BatchAssignRoleReq {
    private List<Integer> userIds;
    private Integer roleId;
}

