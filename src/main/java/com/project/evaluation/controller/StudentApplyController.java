package com.project.evaluation.controller;

import com.project.evaluation.entity.EvaluationPublicity;
import com.project.evaluation.entity.EvaluationSubmitTip;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.ApplyAppealService;
import com.project.evaluation.service.EvaluationObjectionService;
import com.project.evaluation.service.StudentApplyService;
import com.project.evaluation.vo.ApplyAppeal.SubmitAppealReq;
import com.project.evaluation.vo.StudentApply.MaterialUploadVO;
import com.project.evaluation.vo.StudentApply.MyApplyVO;
import com.project.evaluation.vo.StudentApply.RuleItemSimpleVO;
import com.project.evaluation.vo.StudentApply.StudentRuleCategoryTreeNodeVO;
import com.project.evaluation.vo.StudentApply.StudentPeriodWorkflowVO;
import com.project.evaluation.vo.StudentApply.StudentCategoryScoreOverviewVO;
import com.project.evaluation.vo.StudentApply.StudentSectionScoreVO;
import com.project.evaluation.vo.StudentApply.SubmitApplyReq;
import com.project.evaluation.vo.StudentApply.SubmitObjectionReq;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public Result<List<RuleItemSimpleVO>> listRuleItems(@RequestParam Long periodId) {
        return Result.success(studentApplyService.listRuleItems(periodId));
    }

    /**
     * 学生端：按可见分类树返回细则项（用于树形选择）
     */
    @GetMapping("/category-rule-tree")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<List<StudentRuleCategoryTreeNodeVO>> categoryRuleTree(@RequestParam Long periodId) {
        return Result.success(studentApplyService.listRuleItemCategoryTree(periodId));
    }

    /**
     * 学生端：提交申报（细则项；或非细则项+材料+备注+自填分值；任职分另见德育面板）
     */
    @PostMapping("/submit")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<?> submit(@Valid @RequestBody SubmitApplyReq req) {
        studentApplyService.submitApply(req);
        return Result.success();
    }

    /**
     * 学生端：上传申报材料至 MinIO（返回对象键，提交申报时随材料列表一并写入）
     */
    @PostMapping(value = "/upload-material", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<MaterialUploadVO> uploadMaterial(@RequestPart("file") MultipartFile file) {
        return Result.success(studentApplyService.uploadApplyMaterial(file));
    }

    /**
     * 学生端：为本人已上传的材料生成短期可预览地址（图片/PDF 等）
     */
    @GetMapping("/material/preview-url")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<String> materialPreviewUrl(@RequestParam("key") String key) {
        return Result.success(studentApplyService.buildMaterialPreviewUrl(key));
    }

    /**
     * 学生端：查询我提交的申报
     */
    @GetMapping("/mine")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<List<MyApplyVO>> mine() {
        return Result.success(studentApplyService.listMyApplyItems());
    }

    /**
     * 学生端：当前周期得分扁平列表（按分类层级缩进；申报仅统计已通过）
     */
    @GetMapping("/section-scores")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<List<StudentSectionScoreVO>> sectionScores(@RequestParam Long periodId) {
        return Result.success(studentApplyService.listMySectionScores(periodId));
    }

    /**
     * 学生端：当前周期以规则分类为单位的得分树（含分类基础分、细则得分、上限截断后小计与子树汇总）
     */
    @GetMapping("/category-scores")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<StudentCategoryScoreOverviewVO> categoryScores(@RequestParam Long periodId) {
        return Result.success(studentApplyService.listMyCategoryScoreOverview(periodId));
    }

    /**
     * 学生端：对「已驳回」的申报项发起申诉
     */
    @PostMapping("/appeal")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<?> submitAppeal(@Valid @RequestBody SubmitAppealReq req) {
        applyAppealService.submitByStudent(req);
        return Result.success();
    }

    /**
     * 学生端：当前周期阶段与窗口说明
     */
    @GetMapping("/period-workflow")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<StudentPeriodWorkflowVO> periodWorkflow(@RequestParam Long periodId) {
        return Result.success(studentApplyService.getStudentPeriodWorkflow(periodId));
    }

    /**
     * 学生端：公示期内确认无异议（锁定后续申报）
     */
    @PostMapping("/period-confirm-no-objection")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<?> confirmNoObjection(@RequestParam Long periodId) {
        studentApplyService.confirmPeriodNoObjection(periodId);
        return Result.success();
    }

    /**
     * 学生端：当前生效中的公示列表（按班级/全院过滤）
     */
    @GetMapping("/active-publicity")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<List<EvaluationPublicity>> activePublicity(@RequestParam Long periodId) {
        return Result.success(studentApplyService.listActivePublicityForStudent(periodId));
    }

    /**
     * 学生端：分类申报动态提示（教师/管理员维护）
     */
    @GetMapping("/tips")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<List<EvaluationSubmitTip>> tips(@RequestParam Long periodId,
                                                  @RequestParam(required = false) String sectionCode) {
        return Result.success(studentApplyService.listSubmitTipsForStudent(periodId, sectionCode));
    }

    /**
     * 学生端：提交异议
     */
    @PostMapping("/objection")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<?> submitObjection(@Valid @RequestBody SubmitObjectionReq req) {
        evaluationObjectionService.submitByStudent(req);
        return Result.success();
    }
}
