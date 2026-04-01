package com.project.evaluation.service;

import com.project.evaluation.entity.EvaluationPublicity;
import com.project.evaluation.entity.EvaluationSubmitTip;
import com.project.evaluation.vo.StudentApply.MyApplyVO;
import com.project.evaluation.vo.StudentApply.RuleItemSimpleVO;
import com.project.evaluation.vo.StudentApply.StudentPeriodWorkflowVO;
import com.project.evaluation.vo.StudentApply.StudentSectionScoreVO;
import com.project.evaluation.vo.StudentApply.SubmitApplyReq;

import java.util.List;

public interface StudentApplyService {
    List<RuleItemSimpleVO> listRuleItems(Long periodId);

    void submitApply(SubmitApplyReq req);

    List<MyApplyVO> listMyApplyItems();

    StudentPeriodWorkflowVO getStudentPeriodWorkflow(Long periodId);

    void confirmPeriodNoObjection(Long periodId);

    List<EvaluationPublicity> listActivePublicityForStudent(Long periodId);

    List<EvaluationSubmitTip> listSubmitTipsForStudent(Long periodId, String sectionCode);

    /** 当前周期：各综测大类已得分（申报仅统计已通过；智育为管理端录入） */
    List<StudentSectionScoreVO> listMySectionScores(Long periodId);
}
