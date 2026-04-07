package com.project.evaluation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限树节点（部分场景使用），与权限表结构类似；父级 id 为字符串以兼容历史数据。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Authority implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 权限显示名称 */
    private String permName;

    /** 权限编码（唯一业务键） */
    private String permCode;

    /** 父级权限 id（字符串形式） */
    private String parentId;

    /** 状态：1 启用，0 停用 */
    private Integer status;

    /** 记录创建时间 */
    private LocalDateTime createTime;
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;
}
