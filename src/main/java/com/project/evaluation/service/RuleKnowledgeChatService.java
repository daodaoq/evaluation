package com.project.evaluation.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

/**
 * 调用向量检索增强后的模型，回答与综测细则相关的加分规则问题。
 */
@Service
@Profile("ai")
public class RuleKnowledgeChatService {

    /**
     * 系统提示：强调仅供参考，最终以学院审核为准。
     */
    public static final String SYSTEM_PROMPT = """
            你是山东理工大学计算机学院综合素质评价（综测）政策助手。请仅根据对话中提供的「检索到的细则片段」回答问题；若片段不足以判断，请明确说明并建议学生查阅正式文件或咨询辅导员。
            回答使用简体中文，条理清晰。禁止编造细则中不存在的条款。
            若检索片段中同时出现「基础加分明细 / 积极参加…考试」类与「科创竞赛获奖加分」等级表（含多级多档分值如0.6），而用户问的是「英语六级/四六级/参加考试」等，应优先采用「积极参加全国大学生英语四、六级考试」对应条款，并简要说明其与「竞赛获奖按等级加分」不是同一类；必要时列出可能相关的多条并请用户补充「参加考试」还是「竞赛获奖」。
            重要声明：你的回答仅供参考，不构成学院最终认定依据；实际加分以学院审核与系统记录为准。
            """;

    private final ChatClient ruleKnowledgeChatClient;

    public RuleKnowledgeChatService(@Qualifier("ruleKnowledgeChatClient") ChatClient ruleKnowledgeChatClient) {
        this.ruleKnowledgeChatClient = ruleKnowledgeChatClient;
    }

    public String chat(String question) {
        if (!StringUtils.hasText(question)) {
            throw new IllegalArgumentException("请输入问题");
        }
        String q = question.trim();
        if (q.length() > 4000) {
            throw new IllegalArgumentException("问题过长，请控制在 4000 字以内");
        }
        return ruleKnowledgeChatClient
                .prompt()
                .user(q)
                .call()
                .content();
    }

    /** 流式输出模型增量文本（SSE 由 Controller 写出） */
    public Flux<String> chatStream(String question) {
        if (!StringUtils.hasText(question)) {
            return Flux.error(new IllegalArgumentException("请输入问题"));
        }
        String q = question.trim();
        if (q.length() > 4000) {
            return Flux.error(new IllegalArgumentException("问题过长，请控制在 4000 字以内"));
        }
        return ruleKnowledgeChatClient.prompt().user(q).stream().content();
    }
}
