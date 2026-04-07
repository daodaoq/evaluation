package com.project.evaluation.vo.RuleKnowledge;

import lombok.Data;

/**
 * 学生端综测细则知识库问答请求。
 */
@Data
public class RuleKnowledgeChatReq {
    /** 用户问题（自然语言） */
    private String question;
}
