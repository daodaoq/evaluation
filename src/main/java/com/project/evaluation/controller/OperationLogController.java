package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.entity.SysOperationLog;
import com.project.evaluation.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys-operation-log")
public class OperationLogController {

    @Autowired
    private SysOperationLogService sysOperationLogService;

    /**
     * 操作日志分页列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:log:menu')")
    public Result<PageBean<SysOperationLog>> list(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String keyword
    ) {
        return Result.success(sysOperationLogService.pageLogs(pageNum, pageSize, studentId, keyword));
    }
}

