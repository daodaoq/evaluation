package com.project.evaluation.controller;

import com.project.evaluation.entity.Result;
import com.project.evaluation.vo.RuleKnowledge.RuleKnowledgeChatReq;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

/**
 * 未启用 {@code ai} profile 时占位，避免请求落到静态资源处理器产生误导性异常。
 * 启用细则问答请使用 {@code SPRING_PROFILES_ACTIVE=default,ai}（见 evaluation/.env）。
 */
@RestController
@RequestMapping("/student-apply/rule-knowledge")
@Profile("!ai")
public class StudentRuleKnowledgeDisabledController {

    @PostMapping("/chat")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<Map<String, String>> chat(@RequestBody(required = false) RuleKnowledgeChatReq req) {
        return Result.error(
                "细则问答未启用：请在 evaluation 目录的 .env 中设置 SPRING_PROFILES_ACTIVE=default,ai，"
                        + "并配置 AI_API_KEY、启动 Chroma 后重启后端。");
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public SseEmitter chatStream(@RequestBody(required = false) RuleKnowledgeChatReq req) {
        SseEmitter emitter = new SseEmitter(30_000L);
        String msg =
                "细则问答未启用：请在 evaluation 目录的 .env 中设置 SPRING_PROFILES_ACTIVE=default,ai，"
                        + "并配置 AI_API_KEY、启动 Chroma 后重启后端。";
        try {
            emitter.send(SseEmitter.event().name("error").data(msg, MediaType.TEXT_PLAIN));
            emitter.complete();
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }
}
