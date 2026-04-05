package com.project.evaluation.constant;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 学生申报中与得分相关的约定（与前端德育「任职分」自填一致）。
 */
public final class ApplyScoreConstants {

    private ApplyScoreConstants() {}

    /**
     * 管理端「智育」折算为学业水平评价得分的系数（100 分制智育对应 70 分学业上限）。
     */
    public static final BigDecimal INTELLECTUAL_TO_ACADEMIC_RATIO = new BigDecimal("0.7");

    private static final RoundingMode R = RoundingMode.HALF_UP;

    /** 智育分 → 学业水平评价得分（智育 × {@link #INTELLECTUAL_TO_ACADEMIC_RATIO}） */
    public static BigDecimal intellectualToAcademicScore(BigDecimal intellectualScore) {
        if (intellectualScore == null) {
            return BigDecimal.ZERO;
        }
        return intellectualScore.multiply(INTELLECTUAL_TO_ACADEMIC_RATIO).setScale(8, R);
    }

    /** 任职分：非细则项，custom_name 固定为该文案 */
    public static final String POSITION_SCORE_CUSTOM_NAME = "任职分";

    /**
     * 规则分类树中用于挂载「任职分」自填的分类名称（优先）；与 {@link #POSITION_SCORE_CUSTOM_NAME} 二选一存在即可兼容旧库。
     */
    public static final String POSITION_CATEGORY_NAME = "任职";

    /**
     * 挂载管理端智育折算分的规则分类名（与种子数据 {@code evaluation_rule_item_category} 一致）。
     */
    public static final String ACADEMIC_ROOT_CATEGORY_NAME = "学业水平评价";

    /**
     * 子分类「突发加减分」细则名（与 sql/migrate_adhoc_other_score_rule3_categories_18_31.sql 等一致）。
     * 学业模块默认禁止学生申报，此二项除外。
     */
    public static final String ADHOC_OTHER_PLUS_ITEM_NAME = "其他加分（突发·每次0.1）";

    public static final String ADHOC_OTHER_MINUS_ITEM_NAME = "其他减分（突发·每次0.1）";

    public static boolean isStudentAllowedAcademicAdhocRuleItemName(String itemName) {
        if (itemName == null) {
            return false;
        }
        String t = itemName.trim();
        return ADHOC_OTHER_PLUS_ITEM_NAME.equals(t) || ADHOC_OTHER_MINUS_ITEM_NAME.equals(t);
    }

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
