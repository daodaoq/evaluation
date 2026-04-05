package com.project.evaluation.vo.StudentApply;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MyApplyVO {
    private Long applyId;
    private Long periodId;
    private String applyStatus;
    private BigDecimal totalScore;
    private LocalDateTime createTime;
    private Long applyItemId;
    private String itemStatus;
    private Long ruleItemId;
    private String itemName;
    private String sourceType;
    private String customName;
    private String remark;
    /** 最近一次申诉（按 id 最大） */
    private Long appealId;
    private String appealStatus;
    private String appealReason;
    private String appealHandlerRemark;
    /** 本申报项已上传材料（evaluation_apply_material） */
    private List<ApplyMaterialReq> materials = new ArrayList<>();
}
