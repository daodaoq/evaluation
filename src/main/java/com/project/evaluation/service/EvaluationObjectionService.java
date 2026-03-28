package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.vo.Objection.HandleObjectionReq;
import com.project.evaluation.vo.Objection.ObjectionRowVO;
import com.project.evaluation.vo.StudentApply.SubmitObjectionReq;

public interface EvaluationObjectionService {

    void submitByStudent(SubmitObjectionReq req);

    PageBean<ObjectionRowVO> page(Integer pageNum, Integer pageSize, Long periodId, String status, Long classId);

    void handle(HandleObjectionReq req);
}
