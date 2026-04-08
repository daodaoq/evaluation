package com.project.evaluation.score;

import java.math.BigDecimal;

/**
 * CUSTOM：直接使用已落库分值。
 */
public class CustomSourceTypeScoreStrategy implements SourceTypeScoreStrategy {
    @Override
    public BigDecimal effectiveScore(BigDecimal persistedScore, BigDecimal baseScore, BigDecimal coeff, String scoreMode) {
        return persistedScore != null ? persistedScore : BigDecimal.ZERO;
    }
}

