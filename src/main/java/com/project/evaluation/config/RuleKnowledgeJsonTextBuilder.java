package com.project.evaluation.config;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 将《综测细则》结构化 JSON 转为便于向量检索的纯文本（带章节路径）。
 */
public final class RuleKnowledgeJsonTextBuilder {

    private RuleKnowledgeJsonTextBuilder() {}

    public static String toSearchableText(JsonNode root) {
        if (root == null || !root.isObject()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(65536);
        walkObject(sb, root, "");
        return sb.toString();
    }

    private static void walkObject(StringBuilder sb, JsonNode node, String path) {
        node.fields().forEachRemaining(e -> {
            String key = e.getKey();
            JsonNode v = e.getValue();
            String p = path.isEmpty() ? key : path + " / " + key;
            if (v.isObject()) {
                walkObject(sb, v, p);
            } else if (v.isArray()) {
                walkArray(sb, v, p);
            } else {
                appendLeaf(sb, p, valueToString(v));
            }
        });
    }

    private static void walkArray(StringBuilder sb, JsonNode arr, String path) {
        if (arr == null || !arr.isArray() || arr.isEmpty()) {
            return;
        }
        boolean allObjects = true;
        for (JsonNode n : arr) {
            if (!n.isObject()) {
                allObjects = false;
                break;
            }
        }
        sb.append("【").append(path).append("】\n");
        boolean baseBonusDetail = path.contains("基础加分明细");
        if (allObjects) {
            for (JsonNode item : arr) {
                sb.append("  ");
                appendObjectFieldsInline(sb, item);
                sb.append("\n");
                if (baseBonusDetail && item.has("加分内容")) {
                    String content = item.get("加分内容").asText("");
                    if (content.contains("四六级") || content.contains("英语") || content.contains("研究生")) {
                        sb.append(
                                "  ［同义检索：英语六级、大学英语六级、四六级考试、CET、报考、参加考试；本条为「积极参加」类基础加分，非科创竞赛获奖等级表中的分值。］\n");
                    }
                }
            }
            if (baseBonusDetail) {
                sb.append(
                        "  ［段落说明：本小节为创新素养「基础加分明细」，强调积极参加/申报/考试类活动；与同一文档中「科创竞赛获奖加分」（按级别、等级给分，如含0.6）是不同条款，回答「六级/四六级加多少分」时请优先对照本小节。］\n");
            }
        } else {
            for (JsonNode item : arr) {
                if (item.isObject()) {
                    sb.append("  ");
                    appendObjectFieldsInline(sb, item);
                    sb.append("\n");
                } else {
                    sb.append("- ").append(valueToString(item)).append("\n");
                }
            }
        }
        sb.append("\n");
    }

    private static void appendObjectFieldsInline(StringBuilder sb, JsonNode obj) {
        boolean first = true;
        var it = obj.fields();
        while (it.hasNext()) {
            var e = it.next();
            if (!first) {
                sb.append("；");
            }
            first = false;
            sb.append(e.getKey()).append("：").append(valueToString(e.getValue()));
        }
    }

    private static void appendLeaf(StringBuilder sb, String path, String text) {
        sb.append("【").append(path).append("】\n");
        sb.append(text).append("\n\n");
    }

    private static String valueToString(JsonNode n) {
        if (n == null || n.isNull()) {
            return "";
        }
        if (n.isNumber()) {
            return n.numberValue().toString();
        }
        if (n.isBoolean()) {
            return n.booleanValue() ? "是" : "否";
        }
        if (n.isTextual()) {
            return n.asText();
        }
        if (n.isObject()) {
            StringBuilder sb = new StringBuilder();
            appendObjectFieldsInline(sb, n);
            return sb.toString();
        }
        if (n.isArray()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n.size(); i++) {
                if (i > 0) {
                    sb.append("；");
                }
                sb.append(valueToString(n.get(i)));
            }
            return sb.toString();
        }
        return n.asText("");
    }
}
