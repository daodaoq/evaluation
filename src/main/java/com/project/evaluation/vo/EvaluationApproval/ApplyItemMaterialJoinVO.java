package com.project.evaluation.vo.EvaluationApproval;

import lombok.Data;

/** Mapper 批量查询材料行（含 applyItemId 用于组装） */
@Data
public class ApplyItemMaterialJoinVO {
    private Long applyItemId;
    private String fileName;
    private String fileUrl;
}
