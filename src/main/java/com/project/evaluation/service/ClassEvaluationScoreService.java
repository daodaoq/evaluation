package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.vo.ClassScore.ClassEvaluationScoreRowVO;
import com.project.evaluation.vo.ClassScore.ClassUnsubmittedRowVO;

import java.util.List;

public interface ClassEvaluationScoreService {

    PageBean<ClassEvaluationScoreRowVO> page(Integer pageNum, Integer pageSize,
                                             List<Long> periodIds, Long classId, String studentNo, String totalSortOrder);

    byte[] exportExcel(List<Long> periodIds, Long classId, String studentNo, String totalSortOrder);

    PageBean<ClassUnsubmittedRowVO> pageUnsubmitted(Integer pageNum, Integer pageSize,
                                                    List<Long> periodIds, Long classId, String studentNo);

    byte[] exportUnsubmittedExcel(List<Long> periodIds, Long classId, String studentNo);
}
