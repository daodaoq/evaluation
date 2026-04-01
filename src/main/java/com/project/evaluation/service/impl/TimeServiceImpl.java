package com.project.evaluation.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Time;
import com.project.evaluation.mapper.TimeMapper;
import com.project.evaluation.service.PeriodEventLogService;
import com.project.evaluation.service.TimeService;
import com.project.evaluation.vo.Time.AddTimeReq;
import com.project.evaluation.vo.Time.UpdateTimeReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;



import java.util.List;
@Service
@Slf4j
public class TimeServiceImpl implements TimeService {

    @Autowired
    private TimeMapper timeMapper;

    @Autowired
    private PeriodEventLogService periodEventLogService;

    /**
     * 添加周期
     * @param addTimeReq
     */
    @Override
    public void addTime(AddTimeReq addTimeReq) {
        if (addTimeReq.getArchived() == null) {
            addTimeReq.setArchived(0);
        }
        timeMapper.addTime(addTimeReq);
        log.info("添加成功：{}", addTimeReq);
    }

    /**
     * 删除周期
     * @param id
     */
    @Override
    public void deleteTime(Integer id)  {
        if(id == null || id <= 0){
            throw new IllegalArgumentException("非法周期ID");
        }
        int rows = timeMapper.deleteTime(id);
        if (rows == 0) {
            log.warn("删除失败，周期id不存在：{}", id);
            throw new IllegalStateException("周期不存在或已删除");
        }
        periodEventLogService.log(id.longValue(), "PERIOD_DELETE", "删除综测周期");
        log.info("删除周期成功： id={}", id);
    }

    /**
     * 更新周期信息
     * @param updateTimeReq
     */
    @Override
    public void updateTime(Integer id, UpdateTimeReq updateTimeReq) {
        timeMapper.updateTime(id, updateTimeReq);
        periodEventLogService.log(id.longValue(), "PERIOD_UPDATE", "更新周期配置/阶段时间");
    }

    /**
     * 通过周其名称查找周期
     * @param name
     * @return Time
     */
    @Override
    public Time findTimeByName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("周期名称不能为空");
        }
        return timeMapper.findTimeByName(name.trim());
    }

    /**
     * 通过 id 查找周期
     * @param id
     * @return
     */
    @Override
    public Time findTimeById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("非法周期ID");
        }
        return timeMapper.findTimeById(id);
    }

    /**
     * 批量获取综测周期
     * @return
     */
    @Override
    public List<Time> timeList() {
        return timeMapper.timeList();
    }

    /**
     * 分页条件查询周期
     * @param pageNum
     * @param pageSize
     * @param periodName
     * @param status
     * @return
     */
    @Override
    public PageBean<Time> paginationQuery(Integer pageNum, Integer pageSize, String periodName, Integer status) {
        PageBean<Time> pb = new PageBean<>();

        PageHelper.startPage(pageNum, pageSize);

        List<Time> times = timeMapper.paginationQuery(periodName, status);
        // 用 PageInfo 包装，避免将 List 强转为 Page 在部分环境下触发 ClassCastException（会落到全局异常→“系统异常”）
        PageInfo<Time> info = new PageInfo<>(times);
        pb.setTotal(info.getTotal());
        pb.setItems(info.getList());
        return pb;
    }
}
