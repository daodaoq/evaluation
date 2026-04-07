package com.project.evaluation.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统角色，与表 sys_role 对应。
 */
@Data
public class Role {

    /** 主键 */
    private Integer id;

    /** 角色名称（展示用） */
    private String roleName;

    /** 角色编码（唯一标识，如 ROLE_TEACHER） */
    private String roleCode;

    /** 角色说明 */
    private String description;

    /** 状态：1 启用，0 停用 */
    private Integer status;

    /** 记录创建时间 */
    private LocalDateTime createTime;
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;
}
