package com.project.evaluation.vo.StudentApply;

import lombok.Data;

import java.util.List;

@Data
public class SubmitApplyReq {
    private Long periodId;
    private List<ApplyItemReq> items;
    /**
     * 学生主动声明指定「规则分类」下本周期无可申报事项（与 items 互斥）。
     * 为 true 时必须携带 {@link #ruleCategoryId}。
     */
    private Boolean submitNone;

    /**
     * 规则分类 id（evaluation_rule_category.id），仅在与 submitNone 联用时有效。
     */
    private Integer ruleCategoryId;

    /**
     * 声明本周期无可申报任职分（与 items、submitNone 互斥，单独一次请求）。
     */
    private Boolean submitPositionNone;
}
