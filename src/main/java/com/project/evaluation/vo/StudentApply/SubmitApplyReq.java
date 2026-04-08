package com.project.evaluation.vo.StudentApply;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
public class SubmitApplyReq {
    @NotNull(message = "请选择综测周期")
    @Positive(message = "综测周期ID必须为正数")
    private Long periodId;

    @Valid
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

    @AssertTrue(message = "请至少提交一条申报，或使用「本分类无奖项」「无任职分可报」声明")
    public boolean isRequestModePresent() {
        boolean submitNone = Boolean.TRUE.equals(this.submitNone);
        boolean submitPosNone = Boolean.TRUE.equals(this.submitPositionNone);
        return !CollectionUtils.isEmpty(items) || submitNone || submitPosNone;
    }

    @AssertTrue(message = "请勿在同一请求中同时提交「本分类无奖项」与「无任职分可报」")
    public boolean isNoConflictBetweenNoneModes() {
        return !(Boolean.TRUE.equals(submitNone) && Boolean.TRUE.equals(submitPositionNone));
    }

    @AssertTrue(message = "「本分类无奖项」须指定有效规则分类ID")
    public boolean isRuleCategoryProvidedWhenSubmitNone() {
        if (!Boolean.TRUE.equals(submitNone)) {
            return true;
        }
        return ruleCategoryId != null && ruleCategoryId > 0;
    }
}
