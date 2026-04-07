package com.project.evaluation.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生端申报页动态提示（按综测周期与分类），与表 evaluation_student_submit_tip 对应。
 */
@Data
public class EvaluationSubmitTip {
    /** 主键 */
    private Long id;
    /** 综测周期 id */
    private Long periodId;
    /**
     * 分类标识：历史分区编码（如 moral）或规则分类 id 的数字字符串（如 31），用于与学生端分类面板关联。
     */
    private String sectionCode;
    /** 提示标题（短） */
    private String title;
    /** 提示正文 */
    private String content;
    /** 同分区内展示顺序，数值越小越靠前 */
    private Integer sortOrder;
    /** 是否启用：1 启用，0 停用 */
    private Integer status;
    /** 最后操作人用户 id（sys_user.id） */
    private Integer operatorUserId;
    /** 记录创建时间 */
    private LocalDateTime createTime;
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;
}
