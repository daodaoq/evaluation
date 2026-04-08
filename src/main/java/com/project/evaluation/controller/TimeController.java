package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.entity.Time;
import com.project.evaluation.exception.BizException;
import com.project.evaluation.exception.ErrorCode;
import com.project.evaluation.service.TimeService;
import com.project.evaluation.vo.Time.AddTimeReq;
import com.project.evaluation.vo.Time.DeleteTimeReq;
import com.project.evaluation.vo.Time.UpdateTimeReq;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/evaluation-period")
public class TimeController {

    @Autowired
    private TimeService timeService;

    /**
     * 添加周期
     * @param addTimeReq
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('sys:period:flow:menu','sys:rule:menu')")
    public Result<Integer> addTime(@Valid @RequestBody AddTimeReq addTimeReq){
        Time time = timeService.findTimeByName(addTimeReq.getPeriodName());
        if(time == null) {
            timeService.addTime(addTimeReq);
            return Result.success(addTimeReq.getId());
        }
        throw new BizException(ErrorCode.BIZ_CONFLICT, "已有周期");
    }

    /**
     * 删除周期
     * @return
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:period:flow:menu')")
    public Result<?> deleteTime(@Valid @RequestBody DeleteTimeReq deleteTimeReq) throws IllegalAccessException {
        timeService.deleteTime(deleteTimeReq.getId());
        return Result.success();
    }

    /**
     *更新周期信息
     * @param updateTimeReq
     * @return
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:period:flow:menu')")
    public Result<?> updateTime(@Valid @RequestBody UpdateTimeReq updateTimeReq, @PathVariable("id") Integer id) {
        Time time = timeService.findTimeById(id);
        if (time != null) {
            timeService.updateTime(id, updateTimeReq);
            return Result.success();
        }
        throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "更新的周期不存在");
    }

    /**
     * 批量获取周期列表
     * @return
     */
    @GetMapping
    public Result<List<Time>> timeList() {
        List<Time> times = timeService.timeList();
        return Result.success(times);
    }

    /**
     * 查询单个综测周期
     *
     */
    @GetMapping("/{id}")
    public Result<Time> findTimeById(@PathVariable ("id") Integer id){
        Time time = timeService.findTimeById(id);
        return Result.success(time);
    }

    /**
     * 分页条件查询周期
     * @param pageNum
     * @param pageSize
     * @param periodName
     * @param status
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:period:flow:menu')")
    public Result<PageBean<Time>> paginationQuery(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam (required = false) String periodName,
            @RequestParam (required = false) List<Integer> statuses
            ) {
        PageBean<Time> pb = timeService.paginationQuery(pageNum, pageSize, periodName, statuses);
        return Result.success(pb);
   }

}
