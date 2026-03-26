package com.project.evaluation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "sensitive")
public class SensitiveWordProperties {
    /**
     * 是否启用全局敏感词替换
     */
    private boolean enabled = true;

    /**
     * 替换字符，默认 *
     */
    private String maskChar = "*";

    /**
     * 敏感词文件（classpath 或 file 路径）
     */
    private String wordsFile = "classpath:sensitive-words.txt";

    /**
     * 额外配置的敏感词（可选）
     */
    private List<String> words = new ArrayList<>();
}

