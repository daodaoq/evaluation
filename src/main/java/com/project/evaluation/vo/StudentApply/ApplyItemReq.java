package com.project.evaluation.vo.StudentApply;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ApplyItemReq {
    @Positive(message = "细则项ID必须为正数")
    private Long ruleItemId;

    @Size(max = 100, message = "自定义名称长度不能超过100")
    private String customName;

    @Size(max = 1000, message = "备注长度不能超过1000")
    private String remark;
    /**
     * 自填分值：任职分、非细则项必填；细则项勿传。
     */
    @DecimalMin(value = "0", inclusive = false, message = "自填分值必须大于0")
    private BigDecimal declaredScore;

    @Valid
    private List<ApplyMaterialReq> materials;

    /**
     * 细则项：一次提交生成多条相同待审记录（共用材料与备注），默认 1，范围 1～99。
     */
    @Min(value = 1, message = "数量最小为1")
    @Max(value = 99, message = "数量最大为99")
    private Integer quantity;

    /**
     * 细则项：相对「基础分×细则系数」的比例，0.01～1，默认 1；与 {@link #quantity} 独立（每条记录均乘该比例）。
     */
    @DecimalMin(value = "0.01", message = "比例最小为0.01")
    @DecimalMax(value = "1.00", message = "比例最大为1")
    private BigDecimal scoreRatio;
}
