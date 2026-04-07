package com.project.evaluation.controller;

import com.project.evaluation.entity.Result;
import com.project.evaluation.service.RuleKnowledgeChatService;
import com.project.evaluation.vo.RuleKnowledge.RuleKnowledgeChatReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Map;

/**
 * 学生端：基于综测细则知识库的问答（RAG）。
 */
@RestController
@RequestMapping("/student-apply/rule-knowledge")
@Profile("ai")
public class StudentRuleKnowledgeController {

    @Autowired
    private RuleKnowledgeChatService ruleKnowledgeChatService;

    @PostMapping("/chat")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<Map<String, String>> chat(@RequestBody RuleKnowledgeChatReq req) {
        String answer = ruleKnowledgeChatService.chat(req.getQuestion());
        return Result.success(Map.of("answer", answer));
    }

    /**
     * 流式问答（SSE）：事件名 {@code delta} 为正文片段，{@code error} 为可展示错误文案（仍返回 200，便于前端统一读流）。
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public SseEmitter chatStream(@RequestBody(required = false) RuleKnowledgeChatReq req) {
        SseEmitter emitter = new SseEmitter(120_000L);
        String question = req != null ? req.getQuestion() : null;
        Flux<String> flux = ruleKnowledgeChatService.chatStream(question);
        Disposable[] holder = new Disposable[1];
        emitter.onCompletion(
                () -> {
                    Disposable d = holder[0];
                    if (d != null && !d.isDisposed()) {
                        d.dispose();
                    }
                });
        emitter.onTimeout(
                () -> {
                    Disposable d = holder[0];
                    if (d != null && !d.isDisposed()) {
                        d.dispose();
                    }
                });
        holder[0] =
                flux.subscribe(
                        chunk -> {
                            if (chunk == null || chunk.isEmpty()) {
                                return;
                            }
                            try {
                                emitter.send(
                                        SseEmitter.event()
                                                .name("delta")
                                                .data(chunk, MediaType.TEXT_PLAIN));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        err -> {
                            try {
                                String msg =
                                        err.getMessage() != null && !err.getMessage().isBlank()
                                                ? err.getMessage()
                                                : "生成失败，请稍后重试";
                                emitter.send(
                                        SseEmitter.event()
                                                .name("error")
                                                .data(msg, MediaType.TEXT_PLAIN));
                            } catch (IOException ignored) {
                                // ignore
                            }
                            emitter.complete();
                        },
                        emitter::complete);
        return emitter;
    }
}
