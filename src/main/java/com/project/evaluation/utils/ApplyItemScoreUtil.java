package com.project.evaluation.utils;

import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 申报项得分：库内 score 与细则 base/coeff/score_mode 的换算（与 ComprehensiveScoreCalculator 中单条 ADD/SUB 一致）。
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
}
