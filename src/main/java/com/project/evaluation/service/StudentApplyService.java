package com.project.evaluation.service;

import com.project.evaluation.entity.EvaluationPublicity;
import com.project.evaluation.vo.StudentApply.MyApplyVO;
import com.project.evaluation.vo.StudentApply.RuleItemSimpleVO;
import com.project.evaluation.vo.StudentApply.StudentPeriodWorkflowVO;
import com.project.evaluation.vo.StudentApply.SubmitApplyReq;

import java.util.List;

public interface StudentApplyService {
    List<RuleItemSimpleVO> listRuleItems(Long periodId);

    void submitApply(SubmitApplyReq req);

    List<MyApplyVO> listMyApplyItems();

    StudentPeriodWorkflowVO getStudentPeriodWorkflow(Long periodId);

    void confirmPeriodNoObjection(Long periodId);

    List<EvaluationPublicity> listActivePublicityForStudent(Long periodId);
}
