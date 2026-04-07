package com.project.evaluation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统菜单/权限节点，与表 sys_permission 一致，字段与前端 camelCase 对齐。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysPermission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;
    /** 权限显示名称 */
    private String permName;
    /** 权限编码（如 sys:user:menu），用于鉴权与路由 */
    private String permCode;
    /** 父级权限 id；0 表示顶级菜单/分组 */
    private Long parentId;
    /** 状态：1 启用，0 停用 */
    private Integer status;
    /** 记录创建时间 */
    private LocalDateTime createTime;
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;
}
