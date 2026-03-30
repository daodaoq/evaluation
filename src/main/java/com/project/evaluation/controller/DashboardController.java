package com.project.evaluation.controller;

import com.project.evaluation.entity.Result;
import com.project.evaluation.service.DashboardAnalysisService;
import com.project.evaluation.vo.Dashboard.DashboardAnalysisVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardAnalysisService dashboardAnalysisService;

    @GetMapping("/analysis")
    @CrossOrigin
    public Result<DashboardAnalysisVO> analysis(@RequestParam(required = false) Integer periodId) {
        DashboardAnalysisVO vo = dashboardAnalysisService.getDashboardAnalysis(periodId);
        return Result.success(vo);
    }
}

