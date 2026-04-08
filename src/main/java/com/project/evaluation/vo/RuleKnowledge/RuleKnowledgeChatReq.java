package com.project.evaluation.vo.RuleKnowledge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 学生端综测细则知识库问答请求。
 */
@Data
public class RuleKnowledgeChatReq {
    /** 用户问题（自然语言） */
    @NotBlank(message = "请输入问题")
    @Size(max = 4000, message = "问题过长，请控制在 4000 字以内")
    private String question;
}
