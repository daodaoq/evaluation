package com.project.evaluation.controller;

import com.project.evaluation.entity.EvaluationPublicity;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.ApplyAppealService;
import com.project.evaluation.service.EvaluationObjectionService;
import com.project.evaluation.service.StudentApplyService;
import com.project.evaluation.vo.ApplyAppeal.SubmitAppealReq;
import com.project.evaluation.vo.StudentApply.MyApplyVO;
import com.project.evaluation.vo.StudentApply.RuleItemSimpleVO;
import com.project.evaluation.vo.StudentApply.StudentPeriodWorkflowVO;
import com.project.evaluation.vo.StudentApply.SubmitApplyReq;
import com.project.evaluation.vo.StudentApply.SubmitObjectionReq;
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

    @Autowired
    private EvaluationObjectionService evaluationObjectionService;

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

    /**
     * 学生端：当前周期阶段与窗口说明
     */
    @GetMapping("/period-workflow")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<StudentPeriodWorkflowVO> periodWorkflow(@RequestParam Long periodId) {
        return Result.success(studentApplyService.getStudentPeriodWorkflow(periodId));
    }

    /**
     * 学生端：公示期内确认无异议（锁定后续申报）
     */
    @PostMapping("/period-confirm-no-objection")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<?> confirmNoObjection(@RequestParam Long periodId) {
        studentApplyService.confirmPeriodNoObjection(periodId);
        return Result.success();
    }

    /**
     * 学生端：当前生效中的公示列表（按班级/全院过滤）
     */
    @GetMapping("/active-publicity")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<List<EvaluationPublicity>> activePublicity(@RequestParam Long periodId) {
        return Result.success(studentApplyService.listActivePublicityForStudent(periodId));
    }

    /**
     * 学生端：提交异议
     */
    @PostMapping("/objection")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<?> submitObjection(@RequestBody SubmitObjectionReq req) {
        evaluationObjectionService.submitByStudent(req);
        return Result.success();
    }
}
