package com.project.evaluation.score;

import java.math.BigDecimal;

/**
 * 按 scoreMode 计算细则分值的策略接口。
 */
public interface RuleModeScoreStrategy {
    BigDecimal calculate(BigDecimal baseScore, BigDecimal coeff, BigDecimal ratio);
}

