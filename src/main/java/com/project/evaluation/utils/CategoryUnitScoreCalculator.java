package com.project.evaluation.utils;

import com.project.evaluation.constant.ApplyScoreConstants;
import com.project.evaluation.entity.RuleCategory;
import com.project.evaluation.scorepolicy.ScorePolicySnapshot;
import com.project.evaluation.vo.StudentApply.StudentCategoryScoreNodeVO;
import com.project.evaluation.vo.StudentApply.StudentCategoryScoreOverviewVO;
import com.project.evaluation.vo.StudentApply.StudentScoreExtraRowVO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.springframework.util.StringUtils;

/**
 * 以「规则分类」为单位的得分：细则加分归入其 {@code item_category} 指向的节点，在该节点上做加分上限截断；
 * 再与分类基础分、子分类汇总得到树形得分。
 */
public final class CategoryUnitScoreCalculator {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final RoundingMode R = RoundingMode.HALF_UP;

    private CategoryUnitScoreCalculator() {}

    public static StudentCategoryScoreOverviewVO buildOverview(
            BigDecimal intellectualScore,
            List<ComprehensiveScoreCalculator.RuleItemScoreRow> ruleRowsForDedupe,
            BigDecimal positionCustomScore,
            BigDecimal otherCustomScore,
            List<RuleCategory> allCategories,
            Predicate<Integer> categoryIdVisible,
            ScorePolicySnapshot policy
    ) {
        ScorePolicySnapshot p = policy == null ? ScorePolicySnapshot.defaults() : policy;
        if (ruleRowsForDedupe == null) {
            ruleRowsForDedupe = List.of();
        }
        if (allCategories == null) {
            allCategories = List.of();
        }
        Predicate<Integer> vis = categoryIdVisible != null ? categoryIdVisible : x -> true;

        List<ComprehensiveScoreCalculator.DedupedScoreLine> lines =
                ComprehensiveScoreCalculator.buildDedupedLines(ruleRowsForDedupe, p);

        Map<Integer, BigDecimal> addPart = new HashMap<>();
        Map<Integer, BigDecimal> penPart = new HashMap<>();
        BigDecimal uncatAdd = ZERO;
        BigDecimal uncatPen = ZERO;

        for (ComprehensiveScoreCalculator.DedupedScoreLine ln : lines) {
            Integer cid = ln.itemCategory();
            if (ln.penalty()) {
                if (cid == null) {
                    uncatPen = uncatPen.add(ln.signed());
                } else {
                    penPart.merge(cid, ln.signed(), BigDecimal::add);
                }
            } else if (ln.signed().compareTo(ZERO) > 0) {
                if (cid == null) {
                    uncatAdd = uncatAdd.add(ln.signed());
                } else {
                    addPart.merge(cid, ln.signed(), BigDecimal::add);
                }
            }
        }

        Integer positionCatId = findPositionCategoryId(allCategories);
        BigDecimal positionExtra = nz(positionCustomScore);
        if (positionExtra.compareTo(ZERO) > 0 && positionCatId != null) {
            addPart.merge(positionCatId, positionExtra, BigDecimal::add);
            positionExtra = ZERO;
        }

        for (RuleCategory c : allCategories) {
            if (c == null || c.getId() == null || c.getScoreCap() == null) {
                continue;
            }
            if (c.getScoreCap().compareTo(ZERO) < 0) {
                continue;
            }
            int id = c.getId();
            BigDecimal add = addPart.getOrDefault(id, ZERO);
            if (add.compareTo(c.getScoreCap()) > 0) {
                addPart.put(id, c.getScoreCap());
            }
        }

        List<RuleCategory> visible = allCategories.stream()
                .filter(c -> c != null && c.getId() != null && vis.test(c.getId()))
                .sorted(Comparator
                        .comparing(RuleCategory::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(RuleCategory::getId))
                .toList();

        Map<Integer, StudentCategoryScoreNodeVO> nodes = new LinkedHashMap<>();
        for (RuleCategory c : visible) {
            StudentCategoryScoreNodeVO n = new StudentCategoryScoreNodeVO();
            n.setCategoryId(c.getId());
            n.setParentId(c.getParentId());
            n.setCategoryName(c.getCategoryName());
            n.setSortOrder(c.getSortOrder() != null ? c.getSortOrder() : 0);
            n.setCategoryBaseScore(scale(nz(categoryBase(c))));
            n.setScoreCap(c.getScoreCap());
            int id = c.getId();
            BigDecimal itemNet = addPart.getOrDefault(id, ZERO).add(penPart.getOrDefault(id, ZERO));
            n.setItemScore(scale(itemNet));
            n.setChildren(new ArrayList<>());
            nodes.put(id, n);
        }

        StudentCategoryScoreNodeVO uncatNode = null;
        BigDecimal uncatNet = uncatAdd.add(uncatPen);
        if (uncatNet.compareTo(ZERO) != 0) {
            uncatNode = new StudentCategoryScoreNodeVO();
            uncatNode.setCategoryId(null);
            uncatNode.setCategoryName("未归类细则");
            uncatNode.setCategoryBaseScore(ZERO);
            uncatNode.setScoreCap(null);
            uncatNode.setItemScore(scale(uncatNet));
            uncatNode.setChildren(new ArrayList<>());
        }

        List<StudentCategoryScoreNodeVO> roots = new ArrayList<>();
        for (StudentCategoryScoreNodeVO n : nodes.values()) {
            int pid = n.getParentId() == null || n.getParentId() == 0 ? 0 : n.getParentId();
            if (pid == 0) {
                roots.add(n);
            } else {
                StudentCategoryScoreNodeVO parent = nodes.get(pid);
                if (parent != null) {
                    parent.getChildren().add(n);
                } else {
                    roots.add(n);
                }
            }
        }

        sortChildrenRecursive(roots);

        for (StudentCategoryScoreNodeVO r : roots) {
            rollup(r);
        }
        if (uncatNode != null) {
            rollup(uncatNode);
            roots.add(uncatNode);
        }

        StudentCategoryScoreOverviewVO out = new StudentCategoryScoreOverviewVO();
        out.setCategoryRoots(roots);

        if (intellectualScore != null && intellectualScore.compareTo(ZERO) != 0) {
            StudentScoreExtraRowVO ex = new StudentScoreExtraRowVO();
            ex.setLabel("学业水平（智育）");
            ex.setScore(scale(intellectualScore));
            out.getExtraRows().add(ex);
        }
        if (positionExtra.compareTo(ZERO) != 0) {
            StudentScoreExtraRowVO ex = new StudentScoreExtraRowVO();
            ex.setLabel(ApplyScoreConstants.POSITION_SCORE_CUSTOM_NAME);
            ex.setScore(scale(positionExtra));
            out.getExtraRows().add(ex);
        }
        BigDecimal other = nz(otherCustomScore);
        if (other.compareTo(ZERO) != 0) {
            StudentScoreExtraRowVO ex = new StudentScoreExtraRowVO();
            ex.setLabel("其他非细则申报");
            ex.setScore(scale(other));
            out.getExtraRows().add(ex);
        }

        BigDecimal sumTree = ZERO;
        for (StudentCategoryScoreNodeVO r : roots) {
            sumTree = sumTree.add(nz(r.getTotalScore()));
        }
        BigDecimal extraSum = ZERO;
        for (StudentScoreExtraRowVO ex : out.getExtraRows()) {
            extraSum = extraSum.add(nz(ex.getScore()));
        }
        out.setTotalScore(scale(sumTree.add(extraSum)));
        return out;
    }

    private static Integer findPositionCategoryId(List<RuleCategory> all) {
        if (all == null || all.isEmpty()) {
            return null;
        }
        Integer id = findRuleCategoryIdByExactName(all, ApplyScoreConstants.POSITION_CATEGORY_NAME);
        if (id != null) {
            return id;
        }
        return findRuleCategoryIdByExactName(all, ApplyScoreConstants.POSITION_SCORE_CUSTOM_NAME);
    }

    private static Integer findRuleCategoryIdByExactName(List<RuleCategory> all, String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        String t = name.trim();
        for (RuleCategory c : all) {
            if (c == null || c.getId() == null) {
                continue;
            }
            String n = c.getCategoryName() == null ? "" : c.getCategoryName().trim();
            if (t.equals(n)) {
                return c.getId();
            }
        }
        return null;
    }

    private static BigDecimal categoryBase(RuleCategory c) {
        return c.getCategoryBaseScore() != null ? c.getCategoryBaseScore() : ZERO;
    }

    private static void sortChildrenRecursive(List<StudentCategoryScoreNodeVO> level) {
        if (level == null || level.isEmpty()) {
            return;
        }
        level.sort(Comparator
                .comparing(StudentCategoryScoreNodeVO::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(n -> n.getCategoryId() == null ? 0 : n.getCategoryId()));
        for (StudentCategoryScoreNodeVO n : level) {
            sortChildrenRecursive(n.getChildren());
        }
    }

    private static BigDecimal rollup(StudentCategoryScoreNodeVO n) {
        BigDecimal childSum = ZERO;
        for (StudentCategoryScoreNodeVO ch : n.getChildren()) {
            childSum = childSum.add(rollup(ch));
        }
        BigDecimal base = nz(n.getCategoryBaseScore());
        BigDecimal item = nz(n.getItemScore());
        BigDecimal sub = base.add(item);
        n.setSubtotal(scale(sub));
        BigDecimal tot = sub.add(childSum);
        n.setTotalScore(scale(tot));
        return tot;
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : ZERO;
    }

    private static BigDecimal scale(BigDecimal v) {
        return v.setScale(8, R);
    }
}
