package com.project.evaluation.score;

import java.math.BigDecimal;

/**
 * 按 sourceType 计算有效分值的策略接口。
 */
public interface SourceTypeScoreStrategy {
    BigDecimal effectiveScore(BigDecimal persistedScore, BigDecimal baseScore, BigDecimal coeff, String scoreMode);
}

