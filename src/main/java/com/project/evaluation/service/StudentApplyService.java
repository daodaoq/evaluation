package com.project.evaluation.service;

import com.project.evaluation.entity.EvaluationPublicity;
import com.project.evaluation.entity.EvaluationSubmitTip;
import com.project.evaluation.vo.StudentApply.MyApplyVO;
import com.project.evaluation.vo.StudentApply.RuleItemSimpleVO;
import com.project.evaluation.vo.StudentApply.StudentRuleCategoryTreeNodeVO;
import com.project.evaluation.vo.StudentApply.StudentPeriodWorkflowVO;
import com.project.evaluation.vo.StudentApply.StudentCategoryScoreOverviewVO;
import com.project.evaluation.vo.StudentApply.StudentSectionScoreVO;
import com.project.evaluation.vo.StudentApply.MaterialUploadVO;
import com.project.evaluation.vo.StudentApply.SubmitApplyReq;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentApplyService {
    List<RuleItemSimpleVO> listRuleItems(Long periodId);

    /** 学生端：当前周期可见分类树及挂载的细则项 */
    List<StudentRuleCategoryTreeNodeVO> listRuleItemCategoryTree(Long periodId);

    void submitApply(SubmitApplyReq req);

    List<MyApplyVO> listMyApplyItems();

    StudentPeriodWorkflowVO getStudentPeriodWorkflow(Long periodId);

    void confirmPeriodNoObjection(Long periodId);

    List<EvaluationPublicity> listActivePublicityForStudent(Long periodId);

    List<EvaluationSubmitTip> listSubmitTipsForStudent(Long periodId, String sectionCode);

    /**
     * 当前周期：按规则分类扁平列表（含缩进标题）；与 {@link #listMyCategoryScoreOverview(Long)} 同源，
     * 兼容旧表格；智育、任职分等见 extra 行。
     */
    List<StudentSectionScoreVO> listMySectionScores(Long periodId);

    /** 当前周期：以分类为单位的得分树 + 学业等附加行 */
    StudentCategoryScoreOverviewVO listMyCategoryScoreOverview(Long periodId);

    /** 上传申报材料到 MinIO，返回对象键与展示用文件名 */
    MaterialUploadVO uploadApplyMaterial(MultipartFile file);

    /** 为当前学生自己的材料生成短期可预览的 GET 地址（MinIO 预签名） */
    String buildMaterialPreviewUrl(String objectKey);
}
