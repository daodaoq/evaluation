package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.EvaluationApprovalService;
import com.project.evaluation.vo.EvaluationApproval.AuditApplyItemReq;
import com.project.evaluation.vo.EvaluationApproval.EvaluationApplyItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluation-approval")
public class EvaluationApprovalController {

    @Autowired
    private EvaluationApprovalService evaluationApprovalService;

    /**
     * 申报项审批分页（管理员/教师可用）
     */
    @GetMapping("/items")
    @PreAuthorize("hasAuthority('sys:approval:menu') or hasAuthority('sys:rule:menu') or hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<PageBean<EvaluationApplyItemVO>> pageApplyItems(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) Long periodId,
            @RequestParam(required = false) String applyStatus,
            @RequestParam(required = false) String itemStatus,
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) Long classId) {
        return Result.success(evaluationApprovalService.pageApplyItems(
                pageNum, pageSize, studentNo, periodId, applyStatus, itemStatus, collegeId, classId
        ));
    }

    /**
     * 通过单个申报项
     */
    @PutMapping("/items/approve")
    @PreAuthorize("hasAuthority('sys:approval:menu') or hasAuthority('sys:rule:menu') or hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<?> approveApplyItem(@RequestBody AuditApplyItemReq req) {
        evaluationApprovalService.approveApplyItem(req.getApplyItemId(), req.getRemark());
        return Result.success();
    }

    /**
     * 驳回单个申报项
     */
    @PutMapping("/items/reject")
    @PreAuthorize("hasAuthority('sys:approval:menu') or hasAuthority('sys:rule:menu') or hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<?> rejectApplyItem(@RequestBody AuditApplyItemReq req) {
        evaluationApprovalService.rejectApplyItem(req.getApplyItemId(), req.getRemark());
        return Result.success();
    }
}
