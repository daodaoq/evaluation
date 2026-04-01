package com.project.evaluation.vo.StudentApply;

import lombok.Data;

/**
 * 学生端展示的周期阶段与可操作项（只读说明 + 前端按钮显隐参考）
 */
@Data
public class StudentPeriodWorkflowVO {
    private Long periodId;
    private String periodName;
    private Boolean archived;
    private Boolean studentConfirmed;

    private Boolean applicationWindowOpen;
    private Boolean reviewEnded;
    private Boolean inPublicNoticePhase;
    private Boolean inObjectionWindow;

    private String applicationWindowHint;
    private String publicNoticeHint;
    private String objectionHint;

    /** 学生端展示：管理员在周期里配置的各阶段具体时间（中文，可为 null 表示未配置） */
    private String applicationWindowTimeText;
    private String reviewDeadlineTimeText;
    private String publicNoticeTimeText;
    private String objectionDeadlineTimeText;
}
