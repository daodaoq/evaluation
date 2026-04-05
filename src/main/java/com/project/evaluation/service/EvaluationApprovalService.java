package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.vo.EvaluationApproval.EvaluationApplyItemVO;

import java.util.List;

public interface EvaluationApprovalService {
    PageBean<EvaluationApplyItemVO> pageApplyItems(Integer pageNum, Integer pageSize,
                                                          String studentNo, List<Long> periodIds,
                                                          List<String> applyStatuses, List<String> itemStatuses,
                                                          Long collegeId, Long classId);

    void approveApplyItem(Long applyItemId, String remark);

    void rejectApplyItem(Long applyItemId, String remark);

    /** 申诉通过后：申报项退回待审，并刷新申报单聚合状态 */
    void reopenApplyItem(Long applyItemId);

    /**
     * 审批端预览学生申报材料：校验 file_url 已登记后返回 MinIO 预签名地址或外链。
     */
    String buildMaterialPreviewUrlForAuditor(String fileUrlOrKey);
}
