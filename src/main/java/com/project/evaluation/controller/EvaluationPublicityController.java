package com.project.evaluation.controller;

import com.project.evaluation.entity.EvaluationPublicity;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.EvaluationPublicityService;
import com.project.evaluation.vo.Publicity.PublicitySaveReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluation-publicity")
public class EvaluationPublicityController {

    @Autowired
    private EvaluationPublicityService evaluationPublicityService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:period:flow:menu')")
    @CrossOrigin
    public Result<List<EvaluationPublicity>> list(@RequestParam Long periodId) {
        return Result.success(evaluationPublicityService.listByPeriod(periodId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:period:flow:menu')")
    @CrossOrigin
    public Result<?> add(@RequestBody PublicitySaveReq req) {
        evaluationPublicityService.add(req);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('sys:period:flow:menu')")
    @CrossOrigin
    public Result<?> update(@RequestBody PublicitySaveReq req) {
        evaluationPublicityService.update(req);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:period:flow:menu')")
    @CrossOrigin
    public Result<?> delete(@PathVariable Long id) {
        evaluationPublicityService.delete(id);
        return Result.success();
    }
}
