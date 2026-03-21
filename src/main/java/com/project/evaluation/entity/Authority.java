package com.project.evaluation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Authority implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 权限id
     */
    private Integer id;

    /**
     * 权限名
     */
    private String permName;

    /**
     * 权限编码
     */
    private String permCode;

    /**
     * 权限父 id
     */
    private String parentId;

    /**
     * 权限状态
     */
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
