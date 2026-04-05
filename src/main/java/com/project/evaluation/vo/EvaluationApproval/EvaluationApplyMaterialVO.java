package com.project.evaluation.vo.EvaluationApproval;

import lombok.Data;

/** 审批列表中展示的申报材料（不含内部 id） */
@Data
public class EvaluationApplyMaterialVO {
    private String fileName;
    private String fileUrl;
}
