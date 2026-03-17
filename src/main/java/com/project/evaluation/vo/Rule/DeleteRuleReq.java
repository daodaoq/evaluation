package com.project.evaluation.vo.Rule;

import lombok.Data;

/**
 * 删除规则总览请求体
 */
@Data
public class DeleteRuleReq {
    /**
     * 待删除的 id
     */
    private Integer id;
}
