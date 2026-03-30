package com.project.evaluation.controller;

import com.project.evaluation.entity.EvaluationSubmitTip;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.EvaluationSubmitTipService;
import com.project.evaluation.vo.SubmitTip.SubmitTipSaveReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluation-submit-tip")
public class EvaluationSubmitTipController {

    @Autowired
    private EvaluationSubmitTipService evaluationSubmitTipService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('sys:period:flow:menu','sys:objection:menu')")
    @CrossOrigin
    public Result<List<EvaluationSubmitTip>> list(@RequestParam Long periodId,
                                                  @RequestParam(required = false) String sectionCode) {
        return Result.success(evaluationSubmitTipService.listForManage(periodId, sectionCode));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('sys:period:flow:menu','sys:objection:menu')")
    @CrossOrigin
    public Result<?> add(@RequestBody SubmitTipSaveReq req) {
        evaluationSubmitTipService.add(req);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('sys:period:flow:menu','sys:objection:menu')")
    @CrossOrigin
    public Result<?> update(@RequestBody SubmitTipSaveReq req) {
        evaluationSubmitTipService.update(req);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sys:period:flow:menu','sys:objection:menu')")
    @CrossOrigin
    public Result<?> delete(@PathVariable Long id) {
        evaluationSubmitTipService.delete(id);
        return Result.success();
    }
}
