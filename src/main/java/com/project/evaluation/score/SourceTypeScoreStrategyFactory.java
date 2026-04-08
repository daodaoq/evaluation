package com.project.evaluation.score;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * sourceType 策略工厂。
 */
public final class SourceTypeScoreStrategyFactory {
    private static final SourceTypeScoreStrategy RULE = new RuleSourceTypeScoreStrategy();
    private static final SourceTypeScoreStrategy CUSTOM = new CustomSourceTypeScoreStrategy();
    private static final Map<String, SourceTypeScoreStrategy> STRATEGIES = new HashMap<>();

    static {
        STRATEGIES.put("RULE", RULE);
        STRATEGIES.put("CUSTOM", CUSTOM);
    }

    private SourceTypeScoreStrategyFactory() {}

    public static SourceTypeScoreStrategy get(String sourceType) {
        if (sourceType == null || sourceType.isBlank()) {
            return RULE;
        }
        return STRATEGIES.getOrDefault(sourceType.trim().toUpperCase(Locale.ROOT), RULE);
    }
}

