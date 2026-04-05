package com.project.evaluation.vo.StudentApply;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ApplyItemReq {
    private Long ruleItemId;
    private String customName;
    private String remark;
    /** 仅「任职分」自填时使用，其它申报项勿传 */
    private BigDecimal declaredScore;
    private List<ApplyMaterialReq> materials;
}
