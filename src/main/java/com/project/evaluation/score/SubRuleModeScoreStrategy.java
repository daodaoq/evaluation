package com.project.evaluation.score;

import java.math.BigDecimal;

/**
 * SUB：负向扣分。
 */
public class SubRuleModeScoreStrategy implements RuleModeScoreStrategy {
    private final AddRuleModeScoreStrategy addDelegate = new AddRuleModeScoreStrategy();

    @Override
    public BigDecimal calculate(BigDecimal baseScore, BigDecimal coeff, BigDecimal ratio) {
        return addDelegate.calculate(baseScore, coeff, ratio).negate();
    }
}

