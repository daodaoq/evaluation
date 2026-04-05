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

    /**
     * 细则项：一次提交生成多条相同待审记录（共用材料与备注），默认 1，范围 1～99。
     */
    private Integer quantity;

    /**
     * 细则项：相对「基础分×细则系数」的比例，0.01～1，默认 1；与 {@link #quantity} 独立（每条记录均乘该比例）。
     */
    private BigDecimal scoreRatio;
}
