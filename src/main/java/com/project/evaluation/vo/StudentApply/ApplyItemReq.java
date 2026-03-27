package com.project.evaluation.vo.StudentApply;

import lombok.Data;

import java.util.List;

@Data
public class ApplyItemReq {
    private Long ruleItemId;
    private String customName;
    private String remark;
    private List<ApplyMaterialReq> materials;
}
