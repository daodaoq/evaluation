package com.project.evaluation.service;

import com.project.evaluation.config.SensitiveWordProperties;
import com.project.evaluation.utils.SensitiveWordEngine;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class SensitiveWordService {
    @Autowired
    private SensitiveWordProperties properties;

    @Autowired
    private ResourceLoader resourceLoader;

    private volatile SensitiveWordEngine engine = new SensitiveWordEngine();

    @PostConstruct
    public void init() {
        reload();
    }

    public boolean isEnabled() {
        return properties.isEnabled();
    }

    public String sanitizeByField(String fieldName, String input) {
        if (!isEnabled() || !StringUtils.hasText(input)) return input;
        if (StringUtils.hasText(fieldName) && fieldName.toLowerCase().contains("password")) {
            return input;
        }
        char mask = '*';
        if (StringUtils.hasText(properties.getMaskChar())) {
            mask = properties.getMaskChar().charAt(0);
        }
        return engine.replace(input, mask);
    }

    public synchronized void reload() {
        Set<String> words = new LinkedHashSet<>();
        if (properties.getWords() != null) {
            for (String w : properties.getWords()) {
                if (StringUtils.hasText(w)) words.add(w.trim());
            }
        }
        if (StringUtils.hasText(properties.getWordsFile())) {
            try {
                Resource resource = resourceLoader.getResource(properties.getWordsFile());
                if (resource.exists()) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            String w = line.trim();
                            if (!StringUtils.hasText(w) || w.startsWith("#")) continue;
                            words.add(w);
                        }
                    }
                }
            } catch (Exception ignored) {
                // 配置文件异常时不抛出，避免影响主流程
            }
        }
        SensitiveWordEngine newEngine = new SensitiveWordEngine();
        newEngine.build(words);
        this.engine = newEngine;
    }
}

