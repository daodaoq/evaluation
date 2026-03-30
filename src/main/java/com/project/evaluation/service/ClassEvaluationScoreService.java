package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.vo.ClassScore.ClassEvaluationScoreRowVO;
import com.project.evaluation.vo.ClassScore.ClassUnsubmittedRowVO;

public interface ClassEvaluationScoreService {

    PageBean<ClassEvaluationScoreRowVO> page(Integer pageNum, Integer pageSize,
                                             Long periodId, Long classId, String studentNo, String totalSortOrder);

    byte[] exportExcel(Long periodId, Long classId, String studentNo, String totalSortOrder);

    PageBean<ClassUnsubmittedRowVO> pageUnsubmitted(Integer pageNum, Integer pageSize,
                                                    Long periodId, Long classId, String studentNo);

    byte[] exportUnsubmittedExcel(Long periodId, Long classId, String studentNo);
}
