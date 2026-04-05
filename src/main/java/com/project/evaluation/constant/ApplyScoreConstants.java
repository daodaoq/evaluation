package com.project.evaluation.constant;

/**
 * 学生申报中与得分相关的约定（与前端德育「任职分」自填一致）。
 */
public final class ApplyScoreConstants {

    private ApplyScoreConstants() {}

    /** 任职分：非细则项，custom_name 固定为该文案 */
    public static final String POSITION_SCORE_CUSTOM_NAME = "任职分";

    /**
     * 规则分类树中用于挂载「任职分」自填的分类名称（优先）；与 {@link #POSITION_SCORE_CUSTOM_NAME} 二选一存在即可兼容旧库。
     */
    public static final String POSITION_CATEGORY_NAME = "任职";

    /**
     * 学生声明「本规则分类下无可申报事项」：由服务端写入，remark 中携带 RULE_CATEGORY_ID
     */
    public static final String CATEGORY_SUBMIT_NONE_CUSTOM_NAME = "本分类无奖项申报";

    /** 学生声明本周期无可申报任职分：由服务端写入 */
    public static final String POSITION_SUBMIT_NONE_CUSTOM_NAME = "任职分无申报";

    public static boolean isPositionScoreCustomName(String customName) {
        return customName != null && POSITION_SCORE_CUSTOM_NAME.equals(customName.trim());
    }

    public static boolean isCategorySubmitNoneCustomName(String customName) {
        return customName != null && CATEGORY_SUBMIT_NONE_CUSTOM_NAME.equals(customName.trim());
    }

    public static boolean isPositionSubmitNoneCustomName(String customName) {
        return customName != null && POSITION_SUBMIT_NONE_CUSTOM_NAME.equals(customName.trim());
    }

    /** 规则分类节点是否为「任职分」自填锚点（空分类也不修剪） */
    public static boolean isPositionAnchorCategoryName(String categoryName) {
        if (categoryName == null) {
            return false;
        }
        String t = categoryName.trim();
        return POSITION_CATEGORY_NAME.equals(t) || POSITION_SCORE_CUSTOM_NAME.equals(t);
    }
}
