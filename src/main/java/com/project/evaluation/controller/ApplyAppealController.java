package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.ApplyAppealService;
import com.project.evaluation.vo.ApplyAppeal.ApplyAppealRowVO;
import com.project.evaluation.vo.ApplyAppeal.HandleAppealReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/apply-appeal")
public class ApplyAppealController {

    @Autowired
    private ApplyAppealService applyAppealService;

    /**
     * 教师/管理员：申诉分页（教师仅本班学生）
     */
    @GetMapping("/review/page")
    @PreAuthorize("hasAuthority('sys:appeal:menu')")
    @CrossOrigin
    public Result<PageBean<ApplyAppealRowVO>> page(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) List<Long> periodIds,
            @RequestParam(required = false) List<String> appealStatuses,
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) Long classId) {
        List<Long> pids = periodIds == null ? Collections.emptyList() : periodIds;
        List<String> ast = appealStatuses == null ? Collections.emptyList() : appealStatuses;
        return Result.success(applyAppealService.pageAppeals(
                pageNum, pageSize, studentNo, pids, ast, collegeId, classId));
    }

    /**
     * 教师/管理员：处理申诉
     */
    @PutMapping("/review/handle")
    @PreAuthorize("hasAuthority('sys:appeal:menu')")
    @CrossOrigin
    public Result<?> handle(@RequestBody HandleAppealReq req) {
        applyAppealService.handleAppeal(req);
        return Result.success();
    }
}
