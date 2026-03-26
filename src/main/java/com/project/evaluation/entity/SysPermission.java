package com.project.evaluation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 与表 sys_permission 一致，字段名与前端 {@code SysPermission}（camelCase）对齐。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysPermission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String permName;
    private String permCode;
    /** 0 表示顶级 */
    private Long parentId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
