package com.project.evaluation.utils;

import com.project.evaluation.constant.ApplyScoreConstants;
import com.project.evaluation.scorepolicy.InnovationBucket;
import com.project.evaluation.scorepolicy.ScorePolicySnapshot;

import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对已通过申报做组内取高（dedupe）、劳动/创新子项归类汇总等，并按综测「板块」汇总。
 * 分类维度得分与分类上限见 {@link CategoryUnitScoreCalculator}。
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

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    public record RuleItemScoreRow(
            String itemName,
            String moduleCode,
            String submoduleCode,
            String level,
            BigDecimal baseScore,
            BigDecimal coeff,
            String scoreMode,
            String dedupeGroup,
            Long ruleItemId,
            BigDecimal persistedScore,
            String sourceType,
            Integer itemCategory,
            /** 申报项 id：非空时与同 dedupe_group 的其它通过记录分开参与「同类取高」 */
            Long applyItemId
    ) {
        public static RuleItemScoreRow legacy(
                String itemName,
                String moduleCode,
                String level,
                BigDecimal baseScore,
                BigDecimal coeff,
                String scoreMode
        ) {
            return new RuleItemScoreRow(
                    itemName, moduleCode, null, level, baseScore, coeff, scoreMode,
                    null, null, null, "RULE", null, null);
        }
    }

    /**
     * 去重后的计分行（用于板块汇总或按分类汇总）。
     */
    public record DedupedScoreLine(
            Section section,
            String dedupeGroup,
            String submoduleUpper,
            boolean penalty,
            BigDecimal signed,
            Integer itemCategory
    ) {}

    private ComprehensiveScoreCalculator() {}

    public static Map<Section, BigDecimal> sectionScores(BigDecimal intellectualScore, List<RuleItemScoreRow> approvedRuleRows) {
        return sectionScores(intellectualScore, approvedRuleRows, ScorePolicySnapshot.defaults());
    }

    public static Map<Section, BigDecimal> sectionScores(
            BigDecimal intellectualScore,
            List<RuleItemScoreRow> approvedRuleRows,
            ScorePolicySnapshot policy
    ) {
        ScorePolicySnapshot p = policy == null ? ScorePolicySnapshot.defaults() : policy;
        Map<Section, BigDecimal> scores = new EnumMap<>(Section.class);
        for (Section s : Section.values()) {
            scores.put(s, p.sectionBase(s, ZERO));
        }
        if (intellectualScore != null) {
            scores.put(
                    Section.ACADEMIC,
                    scores.get(Section.ACADEMIC).add(ApplyScoreConstants.intellectualToAcademicScore(intellectualScore)));
        }
        if (approvedRuleRows == null || approvedRuleRows.isEmpty()) {
            return scores;
        }

        List<DedupedScoreLine> lines = buildDedupedLines(approvedRuleRows, p);

        BigDecimal moralPen = ZERO, moralAdd = ZERO;
        BigDecimal acadPen = ZERO, acadAdd = ZERO;
        BigDecimal bmPen = ZERO, bmAdd = ZERO;
        BigDecimal artPen = ZERO, artAdd = ZERO;
        BigDecimal labPen = ZERO, labVolunteer = ZERO, labLang = ZERO;
        BigDecimal innPen = ZERO, innBase = ZERO, innDev = ZERO, innPaper = ZERO, innOtherAdd = ZERO;

        for (DedupedScoreLine ln : lines) {
            if (ln.penalty()) {
                switch (ln.section()) {
                    case MORAL -> moralPen = moralPen.add(ln.signed());
                    case ACADEMIC -> acadPen = acadPen.add(ln.signed());
                    case QUALITY_BODYMIND -> bmPen = bmPen.add(ln.signed());
                    case QUALITY_ART -> artPen = artPen.add(ln.signed());
                    case QUALITY_LABOR -> labPen = labPen.add(ln.signed());
                    case QUALITY_INNOVATION -> innPen = innPen.add(ln.signed());
                    default -> { }
                }
                continue;
            }
            if (ln.signed().compareTo(ZERO) <= 0) {
                continue;
            }
            switch (ln.section()) {
                case MORAL -> moralAdd = moralAdd.add(ln.signed());
                case ACADEMIC -> acadAdd = acadAdd.add(ln.signed());
                case QUALITY_BODYMIND -> bmAdd = bmAdd.add(ln.signed());
                case QUALITY_ART -> artAdd = artAdd.add(ln.signed());
                case QUALITY_LABOR -> {
                    if (p.isLaborVolunteerCapExempt(ln.submoduleUpper(), ln.dedupeGroup())) {
                        labLang = labLang.add(ln.signed());
                    } else {
                        labVolunteer = labVolunteer.add(ln.signed());
                    }
                }
                case QUALITY_INNOVATION -> {
                    InnovationBucket b = p.innovationBucket(ln.dedupeGroup());
                    switch (b) {
                        case BASE -> innBase = innBase.add(ln.signed());
                        case DEV -> innDev = innDev.add(ln.signed());
                        case PAPER -> innPaper = innPaper.add(ln.signed());
                        default -> innOtherAdd = innOtherAdd.add(ln.signed());
                    }
                }
                default -> { }
            }
        }

        BigDecimal innovationAdd = innBase.add(innDev).add(innPaper).add(innOtherAdd);

        BigDecimal positionMoral = sumSelfReportedPositionMoral(approvedRuleRows);
        scores.put(Section.MORAL, scores.get(Section.MORAL).add(moralPen).add(moralAdd).add(positionMoral));
        scores.put(Section.ACADEMIC, scores.get(Section.ACADEMIC).add(acadPen).add(acadAdd));
        scores.put(Section.QUALITY_BODYMIND, scores.get(Section.QUALITY_BODYMIND).add(bmPen).add(bmAdd));
        scores.put(Section.QUALITY_ART, scores.get(Section.QUALITY_ART).add(artPen).add(artAdd));
        scores.put(Section.QUALITY_LABOR, scores.get(Section.QUALITY_LABOR).add(labPen).add(labVolunteer).add(labLang));
        scores.put(Section.QUALITY_INNOVATION, scores.get(Section.QUALITY_INNOVATION).add(innPen).add(innovationAdd));

        return scores;
    }

    /** 细则行去重后的列表（不含 CUSTOM；学业模块仅保留「突发加减分」细则名）。 */
    public static List<DedupedScoreLine> buildDedupedLines(List<RuleItemScoreRow> approvedRuleRows, ScorePolicySnapshot policy) {
        ScorePolicySnapshot p = policy == null ? ScorePolicySnapshot.defaults() : policy;
        List<DedupedScoreLine> raw = new ArrayList<>();
        if (approvedRuleRows != null) {
            for (RuleItemScoreRow row : approvedRuleRows) {
                if (row == null) {
                    continue;
                }
                if (StringUtils.hasText(row.sourceType()) && "CUSTOM".equalsIgnoreCase(row.sourceType().trim())) {
                    continue;
                }
                Section sec = detectSection(row.moduleCode(), row.submoduleCode(), row.level(), row.itemName());
                if (sec == Section.ACADEMIC && !ApplyScoreConstants.isStudentAllowedAcademicAdhocRuleItemName(row.itemName())) {
                    continue;
                }
                BigDecimal signed = ApplyItemScoreUtil.effectiveScore(
                        row.persistedScore(),
                        row.sourceType(),
                        row.baseScore(),
                        row.coeff(),
                        row.scoreMode());
                String mode = row.scoreMode() == null ? "ADD" : row.scoreMode().trim().toUpperCase();
                boolean penalty = "SUB".equals(mode) || "MAX_ONLY".equals(mode);
                String dedupe = row.dedupeGroup() == null ? null : row.dedupeGroup().trim();
                if (row.applyItemId() != null) {
                    String base = (dedupe == null || dedupe.isEmpty()) ? "ROW" : dedupe;
                    dedupe = base + "::ai" + row.applyItemId();
                }
                String subU = row.submoduleCode() == null ? "" : row.submoduleCode().trim().toUpperCase();
                raw.add(new DedupedScoreLine(sec, dedupe, subU, penalty, signed, row.itemCategory()));
            }
        }
        return applyDedupeMax(raw, p);
    }

    private static BigDecimal sumSelfReportedPositionMoral(List<RuleItemScoreRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return ZERO;
        }
        BigDecimal s = ZERO;
        for (RuleItemScoreRow row : rows) {
            if (row == null) {
                continue;
            }
            if (!StringUtils.hasText(row.sourceType()) || !"CUSTOM".equalsIgnoreCase(row.sourceType().trim())) {
                continue;
            }
            String name = row.itemName() == null ? "" : row.itemName().trim();
            if (!ApplyScoreConstants.POSITION_SCORE_CUSTOM_NAME.equals(name)) {
                continue;
            }
            s = s.add(ApplyItemScoreUtil.effectiveScore(
                    row.persistedScore(),
                    row.sourceType(),
                    row.baseScore(),
                    row.coeff(),
                    row.scoreMode()));
        }
        return s;
    }

    private static List<DedupedScoreLine> applyDedupeMax(List<DedupedScoreLine> lines, ScorePolicySnapshot policy) {
        Map<String, DedupedScoreLine> winners = new HashMap<>();
        List<DedupedScoreLine> passthrough = new ArrayList<>();
        for (DedupedScoreLine ln : lines) {
            if (ln.penalty() || ln.signed().compareTo(ZERO) <= 0) {
                passthrough.add(ln);
                continue;
            }
            String g = ln.dedupeGroup() == null ? "" : ln.dedupeGroup().trim();
            if (g.isEmpty() || !policy.isDedupeMax(g)) {
                passthrough.add(ln);
                continue;
            }
            String key = ln.section().name() + "::" + g;
            DedupedScoreLine prev = winners.get(key);
            if (prev == null || ln.signed().compareTo(prev.signed()) > 0) {
                winners.put(key, ln);
            }
        }
        List<DedupedScoreLine> merged = new ArrayList<>(passthrough);
        merged.addAll(winners.values());
        return merged;
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

    public static Section detectSection(String moduleCode, String submoduleCode, String level, String itemName) {
        String mod = moduleCode == null ? "" : moduleCode.trim().toUpperCase();
        if ("MORAL".equals(mod)) {
            return Section.MORAL;
        }
        if ("ACADEMIC".equals(mod)) {
            return Section.ACADEMIC;
        }
        if ("QUALITY".equals(mod) && StringUtils.hasText(submoduleCode)) {
            String s = submoduleCode.trim().toUpperCase();
            if ("BODYMIND".equals(s)) {
                return Section.QUALITY_BODYMIND;
            }
            if ("ART".equals(s) || "MEDIA".equals(s) || "ACTIVITY".equals(s)) {
                return Section.QUALITY_ART;
            }
            if ("LABOR".equals(s) || "LANGUAGE".equals(s)) {
                return Section.QUALITY_LABOR;
            }
            if ("INNOVATION".equals(s) || "PAPER".equals(s)) {
                return Section.QUALITY_INNOVATION;
            }
        }
        String sub = level == null ? "" : level.toLowerCase();
        String name = itemName == null ? "" : itemName.toLowerCase();
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

    public static Section sectionFromStudentCode(String sectionCode) {
        if (!StringUtils.hasText(sectionCode)) {
            return null;
        }
        String c = sectionCode.trim().toLowerCase();
        return switch (c) {
            case "moral" -> Section.MORAL;
            case "quality_bodymind" -> Section.QUALITY_BODYMIND;
            case "quality_art" -> Section.QUALITY_ART;
            case "quality_labor" -> Section.QUALITY_LABOR;
            case "quality_innovation" -> Section.QUALITY_INNOVATION;
            default -> null;
        };
    }

    public static BigDecimal sectionEarned(Map<Section, BigDecimal> sectionScores, String studentSectionCode) {
        Section s = sectionFromStudentCode(studentSectionCode);
        if (s == null || sectionScores == null) {
            return ZERO;
        }
        BigDecimal v = sectionScores.get(s);
        return v != null ? v : ZERO;
    }
}
