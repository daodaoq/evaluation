package com.project.evaluation.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 与学生端申报页一致的模块划分与单条细则计分方式（用于教师端汇总展示）。
 */
public final class ComprehensiveScoreCalculator {

    public enum Section {
        MORAL,
        ACADEMIC,
        QUALITY_BODYMIND,
        QUALITY_ART,
        QUALITY_LABOR,
        QUALITY_INNOVATION
    }

    private static final Map<Section, BigDecimal> SECTION_BASE = new EnumMap<>(Section.class);

    static {
        SECTION_BASE.put(Section.MORAL, bd("10"));
        SECTION_BASE.put(Section.ACADEMIC, bd("0"));
        SECTION_BASE.put(Section.QUALITY_BODYMIND, bd("3"));
        SECTION_BASE.put(Section.QUALITY_ART, bd("0"));
        SECTION_BASE.put(Section.QUALITY_LABOR, bd("0"));
        SECTION_BASE.put(Section.QUALITY_INNOVATION, bd("0"));
    }

    public record RuleItemScoreRow(
            String itemName,
            String moduleCode,
            String level,
            BigDecimal baseScore,
            BigDecimal coeff,
            String scoreMode
    ) {}

    private ComprehensiveScoreCalculator() {}

    public static Map<Section, BigDecimal> sectionScores(BigDecimal intellectualScore, List<RuleItemScoreRow> approvedRuleRows) {
        Map<Section, BigDecimal> scores = new EnumMap<>(Section.class);
        for (Section s : Section.values()) {
            scores.put(s, SECTION_BASE.get(s));
        }
        if (intellectualScore != null) {
            scores.put(Section.ACADEMIC, intellectualScore);
        }
        if (approvedRuleRows == null) {
            return scores;
        }
        for (RuleItemScoreRow row : approvedRuleRows) {
            if (row == null) continue;
            Section sec = detectSection(row.moduleCode(), row.level(), row.itemName());
            if (sec == Section.ACADEMIC) {
                continue;
            }
            BigDecimal base = row.baseScore() != null ? row.baseScore() : BigDecimal.ZERO;
            BigDecimal coeff = row.coeff() != null ? row.coeff() : BigDecimal.ONE;
            BigDecimal actual = base.multiply(coeff);
            String mode = row.scoreMode() == null ? "ADD" : row.scoreMode().trim().toUpperCase();
            BigDecimal delta;
            if ("SUB".equals(mode) || "MAX_ONLY".equals(mode)) {
                delta = actual.negate();
            } else {
                delta = actual;
            }
            scores.put(sec, scores.get(sec).add(delta));
        }
        return scores;
    }

    public static BigDecimal totalScore(Map<Section, BigDecimal> sectionScores) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal v : sectionScores.values()) {
            if (v != null) {
                sum = sum.add(v);
            }
        }
        return sum.setScale(8, RoundingMode.HALF_UP);
    }

    public static Section detectSection(String moduleCode, String level, String itemName) {
        String mod = moduleCode == null ? "" : moduleCode.trim().toUpperCase();
        String sub = level == null ? "" : level.toLowerCase();
        String name = itemName == null ? "" : itemName.toLowerCase();

        if ("MORAL".equals(mod)) {
            return Section.MORAL;
        }
        if ("ACADEMIC".equals(mod)) {
            return Section.ACADEMIC;
        }
        if ("QUALITY".equals(mod)) {
            if (name.contains("身心") || name.contains("体育") || name.contains("心理")) {
                return Section.QUALITY_BODYMIND;
            }
            if (name.contains("审美") || name.contains("人文") || name.contains("宣传") || name.contains("晚会")) {
                return Section.QUALITY_ART;
            }
            if (name.contains("劳动") || name.contains("志愿") || name.contains("英语") || name.contains("宿舍")) {
                return Section.QUALITY_LABOR;
            }
            if (name.contains("创新") || name.contains("论文") || name.contains("科研") || name.contains("竞赛")) {
                return Section.QUALITY_INNOVATION;
            }
        }
        if (sub.contains("国家") || sub.contains("省") || sub.contains("市") || sub.contains("院")) {
            return Section.QUALITY_BODYMIND;
        }
        return Section.QUALITY_INNOVATION;
    }

    private static BigDecimal bd(String s) {
        return new BigDecimal(s);
    }
}
