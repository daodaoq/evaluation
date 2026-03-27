package com.project.evaluation.vo.StudentApply;

import lombok.Data;

import java.util.List;

@Data
public class SubmitApplyReq {
    private Long periodId;
    private List<ApplyItemReq> items;
}
