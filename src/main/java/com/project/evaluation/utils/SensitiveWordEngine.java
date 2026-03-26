package com.project.evaluation.utils;

import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 基于 Trie + AC 自动机的敏感词引擎
 */
public class SensitiveWordEngine {
    private final Node root = new Node();

    public void build(Collection<String> words) {
        if (words == null) return;
        for (String word : words) {
            if (!StringUtils.hasText(word)) continue;
            addWord(word.trim().toLowerCase(Locale.ROOT));
        }
        buildFailLinks();
    }

    public String replace(String text, char mask) {
        if (!StringUtils.hasText(text)) return text;
        String lower = text.toLowerCase(Locale.ROOT);
        boolean[] hit = new boolean[text.length()];
        Node state = root;

        for (int i = 0; i < lower.length(); i++) {
            char ch = lower.charAt(i);
            while (state != root && !state.next.containsKey(ch)) {
                state = state.fail;
            }
            state = state.next.getOrDefault(ch, root);
            if (!state.outputs.isEmpty()) {
                for (int len : state.outputs) {
                    int start = i - len + 1;
                    if (start >= 0) {
                        Arrays.fill(hit, start, i + 1, true);
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            sb.append(hit[i] ? mask : text.charAt(i));
        }
        return sb.toString();
    }

    private void addWord(String word) {
        Node cur = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            cur = cur.next.computeIfAbsent(c, k -> new Node());
        }
        cur.outputs.add(word.length());
    }

    private void buildFailLinks() {
        Queue<Node> queue = new ArrayDeque<>();
        root.fail = root;
        for (Node child : root.next.values()) {
            child.fail = root;
            queue.offer(child);
        }
        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            for (Map.Entry<Character, Node> entry : cur.next.entrySet()) {
                char c = entry.getKey();
                Node nxt = entry.getValue();

                Node f = cur.fail;
                while (f != root && !f.next.containsKey(c)) {
                    f = f.fail;
                }
                if (f.next.containsKey(c) && f.next.get(c) != nxt) {
                    nxt.fail = f.next.get(c);
                } else {
                    nxt.fail = root;
                }
                nxt.outputs.addAll(nxt.fail.outputs);
                queue.offer(nxt);
            }
        }
    }

    private static class Node {
        private final Map<Character, Node> next = new HashMap<>();
        private Node fail;
        private final List<Integer> outputs = new ArrayList<>();
    }
}

