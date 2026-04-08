package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.EvaluationApprovalService;
import com.project.evaluation.vo.EvaluationApproval.AuditApplyItemReq;
import com.project.evaluation.vo.EvaluationApproval.EvaluationApplyItemVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

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
    public Result<PageBean<EvaluationApplyItemVO>> pageApplyItems(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) List<Long> periodIds,
            @RequestParam(required = false) List<String> applyStatuses,
            @RequestParam(required = false) List<String> itemStatuses,
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) Long classId) {
        List<Long> pids = periodIds == null ? Collections.emptyList() : periodIds;
        List<String> ast = applyStatuses == null ? Collections.emptyList() : applyStatuses;
        List<String> ist = itemStatuses == null ? Collections.emptyList() : itemStatuses;
        return Result.success(evaluationApprovalService.pageApplyItems(
                pageNum, pageSize, studentNo, pids, ast, ist, collegeId, classId
        ));
    }

    /**
     * 通过单个申报项
     */
    @PutMapping("/items/approve")
    @PreAuthorize("hasAuthority('sys:approval:menu') or hasAuthority('sys:rule:menu') or hasAuthority('sys:student:menu')")
    public Result<?> approveApplyItem(@Valid @RequestBody AuditApplyItemReq req) {
        evaluationApprovalService.approveApplyItem(req.getApplyItemId(), req.getRemark());
        return Result.success();
    }

    /**
     * 驳回单个申报项
     */
    @PutMapping("/items/reject")
    @PreAuthorize("hasAuthority('sys:approval:menu') or hasAuthority('sys:rule:menu') or hasAuthority('sys:student:menu')")
    public Result<?> rejectApplyItem(@Valid @RequestBody AuditApplyItemReq req) {
        evaluationApprovalService.rejectApplyItem(req.getApplyItemId(), req.getRemark());
        return Result.success();
    }

    /**
     * 审批端获取申报材料预览地址（预签名 URL 或外链）
     */
    @GetMapping("/material/preview-url")
    @PreAuthorize("hasAuthority('sys:approval:menu') or hasAuthority('sys:rule:menu') or hasAuthority('sys:student:menu')")
    public Result<String> materialPreviewUrl(@RequestParam("key") String key) {
        return Result.success(evaluationApprovalService.buildMaterialPreviewUrlForAuditor(key));
    }
}
