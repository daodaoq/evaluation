package com.project.evaluation.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 在 {@link org.springframework.boot.SpringApplication} 启动<strong>之前</strong>加载 .env，
 * 避免仅依赖 spring-dotenv 时 profile 已解析完毕、导致 {@code ai} 未生效。
 * <p>
 * 查找顺序：{@code user.dir/.env} → {@code user.dir/evaluation/.env}
 */
public final class DotenvBootstrap {

    private DotenvBootstrap() {}

    public static void load() {
        Path envFile = resolveEnvFile();
        if (envFile == null || !Files.isRegularFile(envFile)) {
            return;
        }
        Dotenv dotenv = Dotenv.configure()
                .directory(envFile.getParent().toString())
                .filename(envFile.getFileName().toString())
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        for (DotenvEntry e : dotenv.entries()) {
            String key = e.getKey();
            String value = e.getValue();
            if (value == null) {
                continue;
            }
            // 操作系统环境变量优先（便于容器/生产覆盖）
            if (System.getenv(key) != null && !System.getenv(key).isEmpty()) {
                continue;
            }
            if (System.getProperty(key) != null && !System.getProperty(key).isEmpty()) {
                continue;
            }
            System.setProperty(key, value);
        }
        String profiles = dotenv.get("SPRING_PROFILES_ACTIVE");
        if (profiles != null && !profiles.isBlank()) {
            System.setProperty("spring.profiles.active", profiles.trim());
        }
        // RestClient 解析相对路径时，若 base 为 .../v1 无尾部 /，则 embeddings 会变成 .../compatible-mode/embeddings（404）
        ensureTrailingSlashOnAiEndpoint();
    }

    /** 保证 AI_ENDPOINT 以 / 结尾，使 base-url + embeddings / chat/completions 拼成正确百炼路径 */
    private static void ensureTrailingSlashOnAiEndpoint() {
        String v = System.getProperty("AI_ENDPOINT");
        if (v == null || v.isBlank()) {
            v = System.getenv("AI_ENDPOINT");
        }
        if (v == null || v.isBlank()) {
            return;
        }
        String t = v.trim();
        if (!t.endsWith("/")) {
            System.setProperty("AI_ENDPOINT", t + "/");
        }
    }

    static Path resolveEnvFile() {
        String wd = System.getProperty("user.dir");
        if (wd == null) {
            return null;
        }
        Path root = Paths.get(wd);
        Path here = root.resolve(".env");
        if (Files.isRegularFile(here)) {
            return here.toAbsolutePath();
        }
        Path inModule = root.resolve("evaluation").resolve(".env");
        if (Files.isRegularFile(inModule)) {
            return inModule.toAbsolutePath();
        }
        return null;
    }
}
