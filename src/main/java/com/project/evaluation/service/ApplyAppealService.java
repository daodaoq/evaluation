package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.vo.ApplyAppeal.ApplyAppealRowVO;
import com.project.evaluation.vo.ApplyAppeal.HandleAppealReq;
import com.project.evaluation.vo.ApplyAppeal.SubmitAppealReq;

import java.util.List;

public interface ApplyAppealService {

    void submitByStudent(SubmitAppealReq req);

    PageBean<ApplyAppealRowVO> pageAppeals(Integer pageNum, Integer pageSize,
                                                  String studentNo, List<Long> periodIds, List<String> appealStatuses,
                                                  Long collegeId, Long classId);

    void handleAppeal(HandleAppealReq req);
}
