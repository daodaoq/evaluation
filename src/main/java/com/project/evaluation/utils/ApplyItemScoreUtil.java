package com.project.evaluation.utils;

import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 申报项得分：库内 score 与细则 base/coeff/score_mode 的换算。
 * SUB：按正数基础分从模块分中扣减（返回负分）；MAX_ONLY：与 SUB 相同符号规则，用于一票否决等「按固定分值扣减」项（见库内数据）。
 */
public final class ApplyItemScoreUtil {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE = BigDecimal.ONE;

    private ApplyItemScoreUtil() {}

    /**
     * @param persistedScore  库中已存分值（含 0）
     * @param sourceType      RULE / CUSTOM
     * @param baseScore       细则基础分，CUSTOM 或非细则可为 null
     * @param coeff           细则系数，默认按 1
     * @param scoreMode       ADD / SUB / MAX_ONLY 等
     */
    public static BigDecimal effectiveScore(
            BigDecimal persistedScore,
            String sourceType,
            BigDecimal baseScore,
            BigDecimal coeff,
            String scoreMode
    ) {
        if (StringUtils.hasText(sourceType) && "CUSTOM".equalsIgnoreCase(sourceType.trim())) {
            return persistedScore != null ? persistedScore : ZERO;
        }
        if (persistedScore != null && persistedScore.compareTo(ZERO) != 0) {
            return persistedScore;
        }
        BigDecimal base = baseScore != null ? baseScore : ZERO;
        BigDecimal c = coeff != null ? coeff : ONE;
        BigDecimal actual = base.multiply(c);
        String mode = scoreMode == null ? "ADD" : scoreMode.trim().toUpperCase();
        if ("SUB".equals(mode) || "MAX_ONLY".equals(mode)) {
            return actual.negate();
        }
        return actual;
    }

    /**
     * 学生提交细则项时写入库的分值：基础分×系数×比例，SUB/MAX_ONLY 为负。
     *
     * @param ratio 已规范到 (0,1] 的比例，null 按 1
     */
    public static BigDecimal declaredRuleItemScore(
            BigDecimal baseScore,
            BigDecimal coeff,
            String scoreMode,
            BigDecimal ratio
    ) {
        BigDecimal r = ratio == null || ratio.compareTo(ZERO) <= 0 ? ONE : ratio;
        if (r.compareTo(ONE) > 0) {
            r = ONE;
        }
        BigDecimal base = baseScore != null ? baseScore : ZERO;
        BigDecimal c = coeff != null ? coeff : ONE;
        BigDecimal actual = base.multiply(c).multiply(r);
        String mode = scoreMode == null ? "ADD" : scoreMode.trim().toUpperCase();
        if ("SUB".equals(mode) || "MAX_ONLY".equals(mode)) {
            return actual.negate();
        }
        return actual;
    }
}
