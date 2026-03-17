package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Time;
import com.project.evaluation.vo.Time.AddTimeReq;
import com.project.evaluation.vo.Time.UpdateTimeReq;

import java.util.List;

public interface TimeService {
    /**
     * 添加周期
     * @param addTimeReq
     */
    void addTime(AddTimeReq addTimeReq);

    /**
     * 删除周期
     * @param id
     */
    void deleteTime(Integer id) throws IllegalAccessException;

    /**
     * 更新周期信息
     * @param updateTimeReq
     */
    void updateTime(Integer id, UpdateTimeReq updateTimeReq);

    /**
     * 通过周其名称查找周期
     * @param name
     * @return Time
     */
    Time findTimeByName(String name);
    /**
     * 通过id查找周期
     * @param id
     * @return
     */
    Time findTimeById(Integer id);
    /**
     * 批量获取座位列表
     * @return
     */
    List<Time> timeList();

    PageBean<Time> paginationQuery(Integer pageNum, Integer pageSize, String periodName, Integer status);
}


