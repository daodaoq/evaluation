package com.project.evaluation.score;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * scoreMode 策略工厂。
 */
public final class RuleModeScoreStrategyFactory {
    private static final RuleModeScoreStrategy ADD = new AddRuleModeScoreStrategy();
    private static final RuleModeScoreStrategy SUB = new SubRuleModeScoreStrategy();
    private static final Map<String, RuleModeScoreStrategy> STRATEGIES = new HashMap<>();

    static {
        STRATEGIES.put("ADD", ADD);
        STRATEGIES.put("SUB", SUB);
        STRATEGIES.put("MAX_ONLY", SUB);
    }

    private RuleModeScoreStrategyFactory() {}

    public static RuleModeScoreStrategy get(String scoreMode) {
        if (scoreMode == null || scoreMode.isBlank()) {
            return ADD;
        }
        return STRATEGIES.getOrDefault(scoreMode.trim().toUpperCase(Locale.ROOT), ADD);
    }
}

