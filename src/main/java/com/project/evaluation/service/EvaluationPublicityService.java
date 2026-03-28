package com.project.evaluation.service;

import com.project.evaluation.entity.EvaluationPublicity;
import com.project.evaluation.vo.Publicity.PublicitySaveReq;

import java.util.List;

public interface EvaluationPublicityService {

    List<EvaluationPublicity> listByPeriod(Long periodId);

    void add(PublicitySaveReq req);

    void update(PublicitySaveReq req);

    void delete(Long id);

    /** 学生端：当前时间在公示记录窗口内且状态为 OPEN，且全院或匹配本班 */
    List<EvaluationPublicity> listActiveForCurrentStudent(Long periodId);
}
