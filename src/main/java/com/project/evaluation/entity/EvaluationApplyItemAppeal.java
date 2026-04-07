package com.project.evaluation.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生对单条申报项审核结果的申诉，与表 evaluation_apply_item_appeal 对应。
 */
@Data
public class EvaluationApplyItemAppeal {
    /** 主键 */
    private Long id;
    /** 被申诉的申报项 id（evaluation_apply_item.id） */
    private Long applyItemId;
    /** 申诉学生用户 id（sys_user.id，冗余便于查询） */
    private Long studentId;
    /** 申诉理由 */
    private String reason;
    /** 申诉状态：如 PENDING/ACCEPTED/REJECTED */
    private String status;
    /** 处理人用户 id（sys_user.id） */
    private Long handlerId;
    /** 处理说明 */
    private String handlerRemark;
    /** 记录创建时间 */
    private LocalDateTime createTime;
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;
}
