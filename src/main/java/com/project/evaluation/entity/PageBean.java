package com.project.evaluation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页列表通用结构。
 *
 * @param <T> 单条记录类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageBean<T>{
    /** 符合条件的总记录数 */
    private Long total;

    /** 当前页数据列表 */
    private List<T> items;
}