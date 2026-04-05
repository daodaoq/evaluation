package com.project.evaluation.scorepolicy;

import com.project.evaluation.utils.ComprehensiveScoreCalculator.Section;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 综测分项计分策略快照（内置默认；不再从数据库按规则加载）。
 * 板块奖励封顶等数值上限已移除，由业务层后续统一实现。
 */
public final class ScorePolicySnapshot {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final EnumMap<Section, BigDecimal> sectionBases;
    private final Set<String> dedupeMaxGroups;
    private final Set<String> laborExemptSubmodules;
    private final Set<String> laborExemptDedupes;
    private final Map<String, InnovationBucket> innovationBuckets;

    public ScorePolicySnapshot(
            EnumMap<Section, BigDecimal> sectionBases,
            Set<String> dedupeMaxGroups,
            Set<String> laborExemptSubmodules,
            Set<String> laborExemptDedupes,
            Map<String, InnovationBucket> innovationBuckets
    ) {
        EnumMap<Section, BigDecimal> sb = new EnumMap<>(Section.class);
        sb.putAll(sectionBases);
        this.sectionBases = sb;
        this.dedupeMaxGroups = Collections.unmodifiableSet(new HashSet<>(dedupeMaxGroups));
        this.laborExemptSubmodules = Collections.unmodifiableSet(new HashSet<>(laborExemptSubmodules));
        this.laborExemptDedupes = Collections.unmodifiableSet(new HashSet<>(laborExemptDedupes));
        this.innovationBuckets = Collections.unmodifiableMap(new HashMap<>(innovationBuckets));
    }

    public static ScorePolicySnapshot defaults() {
        EnumMap<Section, BigDecimal> sb = new EnumMap<>(Section.class);
        sb.put(Section.MORAL, bd("10"));
        sb.put(Section.ACADEMIC, bd("0"));
        sb.put(Section.QUALITY_BODYMIND, bd("3"));
        sb.put(Section.QUALITY_ART, bd("0"));
        sb.put(Section.QUALITY_LABOR, bd("0"));
        sb.put(Section.QUALITY_INNOVATION, bd("0"));

        Set<String> dedupe = Set.of(
                "MORAL_HONOR_GOOD",
                "MORAL_HONOR_COMMEND",
                "QUALITY_BODYMIND_AWARD",
                "QUALITY_ART_AWARD",
                "QUALITY_LABOR_AWARD",
                "QUALITY_INNOVATION_BASE",
                "QUALITY_INNOVATION_DEV"
        );

        Set<String> laborSub = Set.of("LANGUAGE");
        Set<String> laborDed = Set.of("QUALITY_LANGUAGE");

        Map<String, InnovationBucket> innov = Map.of(
                "QUALITY_INNOVATION_BASE", InnovationBucket.BASE,
                "QUALITY_INNOVATION_DEV", InnovationBucket.DEV,
                "QUALITY_INNOVATION_PAPER", InnovationBucket.PAPER
        );

        return new ScorePolicySnapshot(sb, dedupe, laborSub, laborDed, innov);
    }

    public BigDecimal sectionBase(Section section, BigDecimal fallback) {
        BigDecimal v = sectionBases.get(section);
        return v != null ? v : (fallback != null ? fallback : ZERO);
    }

    public boolean isDedupeMax(String dedupeGroup) {
        if (dedupeGroup == null || dedupeGroup.isBlank()) {
            return false;
        }
        return dedupeMaxGroups.contains(dedupeGroup.trim());
    }

    public boolean isLaborVolunteerCapExempt(String submoduleUpper, String dedupeGroup) {
        String sub = submoduleUpper == null ? "" : submoduleUpper.trim().toUpperCase(Locale.ROOT);
        if (!sub.isEmpty() && laborExemptSubmodules.contains(sub)) {
            return true;
        }
        String d = dedupeGroup == null ? "" : dedupeGroup.trim().toUpperCase(Locale.ROOT);
        return !d.isEmpty() && laborExemptDedupes.contains(d);
    }

    public InnovationBucket innovationBucket(String dedupeGroup) {
        if (dedupeGroup == null || dedupeGroup.isBlank()) {
            return InnovationBucket.OTHER;
        }
        InnovationBucket b = innovationBuckets.get(dedupeGroup.trim());
        return b != null ? b : InnovationBucket.OTHER;
    }

    public EnumMap<Section, BigDecimal> mutableSectionBasesCopy() {
        return new EnumMap<>(sectionBases);
    }

    public Set<String> mutableDedupeMaxCopy() {
        return new HashSet<>(dedupeMaxGroups);
    }

    public Set<String> mutableLaborExemptSubCopy() {
        return new HashSet<>(laborExemptSubmodules);
    }

    public Set<String> mutableLaborExemptDedupeCopy() {
        return new HashSet<>(laborExemptDedupes);
    }

    public Map<String, InnovationBucket> mutableInnovationBucketsCopy() {
        return new HashMap<>(innovationBuckets);
    }

    private static BigDecimal bd(String s) {
        return new BigDecimal(s);
    }
}
