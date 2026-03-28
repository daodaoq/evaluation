package com.project.evaluation.vo.ApplyAppeal;

import lombok.Data;

@Data
public class HandleAppealReq {
    private Long appealId;
    /** ACCEPTED：通过申诉，申报项退回待审；REJECTED：驳回申诉 */
    private String decision;
    private String handlerRemark;
}
