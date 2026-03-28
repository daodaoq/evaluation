package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.entity.Time;
import com.project.evaluation.service.TimeService;
import com.project.evaluation.vo.Time.AddTimeReq;
import com.project.evaluation.vo.Time.DeleteTimeReq;
import com.project.evaluation.vo.Time.UpdateTimeReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAuthority('sys:period:flow:menu')")
    @CrossOrigin
    public Result addTime(@RequestBody AddTimeReq addTimeReq){
        Time time = timeService.findTimeByName(addTimeReq.getPeriodName());
        if(time == null) {
            timeService.addTime(addTimeReq);
            return Result.success();
        } else{
            return Result.error("已有周期");
        }
    }

    /**
     * 删除周期
     * @return
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:period:flow:menu')")
    @CrossOrigin
    public Result deleteTime(@RequestBody DeleteTimeReq deleteTimeReq) throws IllegalAccessException {
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
    @CrossOrigin
    public Result updateTime(@RequestBody UpdateTimeReq updateTimeReq, @PathVariable("id") Integer id) {
        Time time = timeService.findTimeById(id);
        if (time != null) {
            timeService.updateTime(id, updateTimeReq);
            return Result.success();
        } else {
            return Result.error("更新的周期不存在");
        }
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
    @CrossOrigin
    public Result<PageBean<Time>> paginationQuery(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam (required = false) String periodName,
            @RequestParam (required = false) Integer status
            ) {
        PageBean<Time> pb = timeService.paginationQuery(pageNum, pageSize, periodName, status);
        return Result.success(pb);
   }

}
