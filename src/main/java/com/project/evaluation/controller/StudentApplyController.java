package com.project.evaluation.controller;

import com.project.evaluation.entity.Result;
import com.project.evaluation.service.ApplyAppealService;
import com.project.evaluation.service.StudentApplyService;
import com.project.evaluation.vo.ApplyAppeal.SubmitAppealReq;
import com.project.evaluation.vo.StudentApply.MyApplyVO;
import com.project.evaluation.vo.StudentApply.RuleItemSimpleVO;
import com.project.evaluation.vo.StudentApply.SubmitApplyReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student-apply")
public class StudentApplyController {

    @Autowired
    private StudentApplyService studentApplyService;

    @Autowired
    private ApplyAppealService applyAppealService;

    /**
     * 学生端：查询当前周期可申报细则项
     */
    @GetMapping("/rule-items")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<List<RuleItemSimpleVO>> listRuleItems(@RequestParam Long periodId) {
        return Result.success(studentApplyService.listRuleItems(periodId));
    }

    /**
     * 学生端：提交申报（仅细则项，或非细则项+材料+备注）
     */
    @PostMapping("/submit")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<?> submit(@RequestBody SubmitApplyReq req) {
        studentApplyService.submitApply(req);
        return Result.success();
    }

    /**
     * 学生端：查询我提交的申报
     */
    @GetMapping("/mine")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<List<MyApplyVO>> mine() {
        return Result.success(studentApplyService.listMyApplyItems());
    }

    /**
     * 学生端：对「已驳回」的申报项发起申诉
     */
    @PostMapping("/appeal")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<?> submitAppeal(@RequestBody SubmitAppealReq req) {
        applyAppealService.submitByStudent(req);
        return Result.success();
    }
}
