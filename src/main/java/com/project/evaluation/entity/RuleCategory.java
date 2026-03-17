package com.project.evaluation.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RuleCategory {

    // TODO: 将非数据库字段添加进来

    /**
     * 规则分类 id
     */
    private Integer id;

    /**
     * 规则总览 id
     */
    private Integer ruleId;

    /**
     * 分类名
     */
    private String categoryName;

    /**
     * 分类父级 id
     */
    private Integer parentId;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
