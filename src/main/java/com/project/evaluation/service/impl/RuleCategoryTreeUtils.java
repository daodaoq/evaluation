package com.project.evaluation.service.impl;

import com.project.evaluation.entity.RuleCategory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 规则分类树：拓扑序与跨规则 id 对齐（按「名称 + 父级在目标树中的 id」匹配）。
 */
public final class RuleCategoryTreeUtils {

    private RuleCategoryTreeUtils() {
    }

    static int normParentId(Integer parentId) {
        return parentId == null || parentId == 0 ? 0 : parentId;
    }

    /**
     * 父节点始终排在子节点之前；同层按 id 升序，保证与目标树 sibling 配对稳定。
     */
    static List<RuleCategory> topologicalOrder(List<RuleCategory> all) {
        if (all == null || all.isEmpty()) {
            return List.of();
        }
        Map<Integer, List<RuleCategory>> children = new HashMap<>();
        List<RuleCategory> roots = new ArrayList<>();
        for (RuleCategory c : all) {
            int p = normParentId(c.getParentId());
            if (p == 0) {
                roots.add(c);
            } else {
                children.computeIfAbsent(p, k -> new ArrayList<>()).add(c);
            }
        }
        Comparator<RuleCategory> siblingOrder = Comparator
                .comparing(RuleCategory::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(RuleCategory::getId);
        roots.sort(siblingOrder);
        for (List<RuleCategory> list : children.values()) {
            list.sort(siblingOrder);
        }
        List<RuleCategory> out = new ArrayList<>();
        ArrayDeque<RuleCategory> dq = new ArrayDeque<>(roots);
        while (!dq.isEmpty()) {
            RuleCategory c = dq.poll();
            out.add(c);
            for (RuleCategory ch : children.getOrDefault(c.getId(), List.of())) {
                dq.add(ch);
            }
        }
        if (out.size() != all.size()) {
            throw new IllegalStateException("规则分类树数据异常（可能存在环或无效的父级引用）");
        }
        return out;
    }

    /**
     * 将来源规则下的分类主键映射到目标规则下对应节点（结构、名称、父子关系一致）。
     */
    static Map<Integer, Integer> buildSourceToTargetIdMap(List<RuleCategory> source, List<RuleCategory> target) {
        if (source == null || source.isEmpty()) {
            return Map.of();
        }
        if (target == null || target.isEmpty()) {
            throw new IllegalStateException(
                    "目标学期规则下尚无分类，请先在「规则分类管理」中从来源学期复制分类后再复制规则项");
        }
        List<RuleCategory> ordered = topologicalOrder(source);
        Map<Integer, Integer> map = new HashMap<>(ordered.size() * 2);
        Set<Integer> usedTarget = new HashSet<>();
        for (RuleCategory s : ordered) {
            int sp = normParentId(s.getParentId());
            int expectedTargetParent;
            if (sp == 0) {
                expectedTargetParent = 0;
            } else {
                Integer p = map.get(sp);
                if (p == null) {
                    throw new IllegalStateException("规则分类树数据异常：存在悬空父级");
                }
                expectedTargetParent = p;
            }
            RuleCategory t = pickTargetMatch(target, s.getCategoryName(), expectedTargetParent, usedTarget);
            if (t == null) {
                throw new IllegalStateException(
                        "目标规则下分类与来源不一致，请先在「规则分类管理」使用「从学期复制分类」并勾选覆盖后再复制规则项");
            }
            map.put(s.getId(), t.getId());
            usedTarget.add(t.getId());
        }
        return map;
    }

    private static RuleCategory pickTargetMatch(
            List<RuleCategory> target,
            String name,
            int expectedParentId,
            Set<Integer> usedTarget) {
        return target.stream()
                .filter(t -> !usedTarget.contains(t.getId()))
                .filter(t -> Objects.equals(
                        t.getCategoryName() == null ? "" : t.getCategoryName(),
                        name == null ? "" : name))
                .filter(t -> normParentId(t.getParentId()) == expectedParentId)
                .min(Comparator.comparing(RuleCategory::getId))
                .orElse(null);
    }
}
