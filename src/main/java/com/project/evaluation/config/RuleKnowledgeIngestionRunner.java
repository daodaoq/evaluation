package com.project.evaluation.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 启动时将 classpath 下《综测细则》JSON 转为正文后切块写入 Chroma，供 RAG 检索。
 */
@Slf4j
@Component
@Profile("ai")
@Order(Ordered.LOWEST_PRECEDENCE)
public class RuleKnowledgeIngestionRunner implements ApplicationRunner {

    private static final String RULE_RESOURCE = "综测细则.json";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final VectorStore vectorStore;

    @Value("${app.ai.ingest-rules-on-start:true}")
    private boolean ingestOnStart;

    public RuleKnowledgeIngestionRunner(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!ingestOnStart) {
            log.info("已跳过综测细则向量入库（app.ai.ingest-rules-on-start=false）");
            return;
        }
        try {
            ClassPathResource res = new ClassPathResource(RULE_RESOURCE);
            if (!res.exists()) {
                log.warn("classpath 未找到 {}，跳过向量入库", RULE_RESOURCE);
                return;
            }
            JsonNode root;
            try (var in = res.getInputStream()) {
                root = OBJECT_MAPPER.readTree(in);
            }
            String plainText = RuleKnowledgeJsonTextBuilder.toSearchableText(root);
            if (!org.springframework.util.StringUtils.hasText(plainText)) {
                log.warn("{} 解析后正文为空，跳过向量入库", RULE_RESOURCE);
                return;
            }
            Map<String, Object> meta = new HashMap<>();
            meta.put("source", RULE_RESOURCE);
            Document doc = Document.builder().text(plainText).metadata(meta).build();
            TokenTextSplitter splitter = new TokenTextSplitter(800, 100, 5, 10000, true);
            List<Document> chunks = splitter.apply(List.of(doc));
            vectorStore.add(chunks);
            log.info("综测细则已向量化入库，共 {} 个片段", chunks.size());
        } catch (Exception e) {
            log.error("综测细则向量入库失败（请检查 Chroma、Embedding 配置与网络）: {}", e.getMessage());
        }
    }
}
