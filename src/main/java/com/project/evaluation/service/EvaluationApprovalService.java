package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.vo.EvaluationApproval.EvaluationApplyItemVO;

public interface EvaluationApprovalService {
    PageBean<EvaluationApplyItemVO> pageApplyItems(Integer pageNum, Integer pageSize,
                                                   String studentNo, Long periodId,
                                                   String applyStatus, String itemStatus,
                                                   Long collegeId, Long classId);

    void approveApplyItem(Long applyItemId, String remark);

    void rejectApplyItem(Long applyItemId, String remark);

    /** 申诉通过后：申报项退回待审，并刷新申报单聚合状态 */
    void reopenApplyItem(Long applyItemId);
}
