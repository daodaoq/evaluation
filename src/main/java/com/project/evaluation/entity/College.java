package com.project.evaluation.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class College {

    /**
     *学院ID
     */
    private Integer id;

    /**
     * 学院名称
     */
    private String collegeName;

    /**
     * 状态（0停用1启用）
     */
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;


}
