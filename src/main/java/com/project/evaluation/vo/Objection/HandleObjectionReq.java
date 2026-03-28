package com.project.evaluation.vo.Objection;

import lombok.Data;

@Data
public class HandleObjectionReq {
    private Long objectionId;
    /** HANDLED=已处理并采纳说明 / REJECTED=驳回异议 */
    private String decision;
    private String handlerRemark;
}
