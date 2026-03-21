package com.project.evaluation.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 *角色实体类
 */
@Data
public class Role {

    /**
     * id
     */
    private Integer id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色名称代码
     */
    private String roleCode;

    /**
     * 角色解释
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
