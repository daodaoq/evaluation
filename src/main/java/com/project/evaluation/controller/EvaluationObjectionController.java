package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.EvaluationObjectionService;
import com.project.evaluation.vo.Objection.HandleObjectionReq;
import com.project.evaluation.vo.Objection.ObjectionRowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluation-objection")
public class EvaluationObjectionController {

    @Autowired
    private EvaluationObjectionService evaluationObjectionService;

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('sys:objection:menu')")
    @CrossOrigin
    public Result<PageBean<ObjectionRowVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long periodId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long classId) {
        return Result.success(evaluationObjectionService.page(pageNum, pageSize, periodId, status, classId));
    }

    @PostMapping("/handle")
    @PreAuthorize("hasAuthority('sys:objection:menu')")
    @CrossOrigin
    public Result<?> handle(@RequestBody HandleObjectionReq req) {
        evaluationObjectionService.handle(req);
        return Result.success();
    }
}
