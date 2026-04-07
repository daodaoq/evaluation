package com.project.evaluation.config;

import com.project.evaluation.service.RuleKnowledgeChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 综测细则 RAG 问答：基于 Chroma 向量检索 + OpenAI 兼容大模型。
 */
@Configuration
@Profile("ai")
public class RuleKnowledgeAiConfig {

    /** 略增大检索条数，减少「竞赛等级表」挤占「基础加分明细」召回的情况 */
    private static final int RULE_RAG_TOP_K = 12;

    @Bean
    public ChatClient ruleKnowledgeChatClient(ChatModel chatModel, VectorStore vectorStore) {
        SearchRequest searchRequest =
                SearchRequest.builder().topK(RULE_RAG_TOP_K).build();
        QuestionAnswerAdvisor ragAdvisor =
                QuestionAnswerAdvisor.builder(vectorStore).searchRequest(searchRequest).build();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(ragAdvisor)
                .defaultSystem(RuleKnowledgeChatService.SYSTEM_PROMPT)
                .build();
    }
}
