package com.project.evaluation.vo.SubmitTip;

import lombok.Data;

@Data
public class SubmitTipSaveReq {
    private Long id;
    private Long periodId;
    private String sectionCode;
    private String title;
    private String content;
    private Integer sortOrder;
    private Integer status;
}
