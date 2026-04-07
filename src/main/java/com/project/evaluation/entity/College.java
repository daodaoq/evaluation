package com.project.evaluation.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学院，与表 sys_college 对应。
 */
@Data
public class College {

    /** 主键 */
    private Integer id;

    /** 学院名称 */
    private String collegeName;

    /** 状态：0 停用，1 启用 */
    private Integer status;

    /** 记录创建时间 */
    private LocalDateTime createTime;
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;


}
