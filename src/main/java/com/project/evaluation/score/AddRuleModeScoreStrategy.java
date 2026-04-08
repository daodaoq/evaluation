package com.project.evaluation.score;

import java.math.BigDecimal;

/**
 * ADD：正向加分。
 */
public class AddRuleModeScoreStrategy implements RuleModeScoreStrategy {
    @Override
    public BigDecimal calculate(BigDecimal baseScore, BigDecimal coeff, BigDecimal ratio) {
        BigDecimal base = baseScore != null ? baseScore : BigDecimal.ZERO;
        BigDecimal c = coeff != null ? coeff : BigDecimal.ONE;
        BigDecimal r = ratio != null ? ratio : BigDecimal.ONE;
        return base.multiply(c).multiply(r);
    }
}

