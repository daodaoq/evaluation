package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.vo.Objection.HandleObjectionReq;
import com.project.evaluation.vo.Objection.ObjectionRowVO;
import com.project.evaluation.vo.StudentApply.SubmitObjectionReq;

import java.util.List;

public interface EvaluationObjectionService {

    void submitByStudent(SubmitObjectionReq req);

    PageBean<ObjectionRowVO> page(Integer pageNum, Integer pageSize, List<Long> periodIds, List<String> statuses,
                                  Long collegeId, Long classId);

    void handle(HandleObjectionReq req);
}
