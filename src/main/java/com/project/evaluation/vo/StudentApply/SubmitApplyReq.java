package com.project.evaluation.vo.StudentApply;

import lombok.Data;

import java.util.List;

@Data
public class SubmitApplyReq {
    private Long periodId;
    private List<ApplyItemReq> items;
    /**
     * 学生主动声明“本周期无可申报奖项/事项”
     */
    private Boolean submitNone;
}
