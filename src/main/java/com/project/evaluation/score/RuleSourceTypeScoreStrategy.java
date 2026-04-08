package com.project.evaluation.score;

import java.math.BigDecimal;

/**
 * RULE：优先已落库分，未落库则按 scoreMode 动态计算。
 */
public class RuleSourceTypeScoreStrategy implements SourceTypeScoreStrategy {
    @Override
    public BigDecimal effectiveScore(BigDecimal persistedScore, BigDecimal baseScore, BigDecimal coeff, String scoreMode) {
        if (persistedScore != null && persistedScore.compareTo(BigDecimal.ZERO) != 0) {
            return persistedScore;
        }
        return RuleModeScoreStrategyFactory.get(scoreMode).calculate(baseScore, coeff, BigDecimal.ONE);
    }
}

