package com.project.evaluation.service;

import com.project.evaluation.entity.EvaluationSubmitTip;
import com.project.evaluation.vo.SubmitTip.SubmitTipSaveReq;

import java.util.List;

public interface EvaluationSubmitTipService {
    List<EvaluationSubmitTip> listForManage(Long periodId, String sectionCode);

    void add(SubmitTipSaveReq req);

    void update(SubmitTipSaveReq req);

    void delete(Long id);

    List<EvaluationSubmitTip> listForStudent(Long periodId, String sectionCode);
}
