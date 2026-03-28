package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.vo.ClassScore.ClassEvaluationScoreRowVO;

public interface ClassEvaluationScoreService {

    PageBean<ClassEvaluationScoreRowVO> page(Integer pageNum, Integer pageSize,
                                             Long periodId, Long classId, String studentNo);

    byte[] exportExcel(Long periodId, Long classId, String studentNo);
}
