package com.project.evaluation.entity;

import lombok.Data;

/**
 * 申报项附件材料（如 MinIO 对象键），与表 evaluation_apply_material 对应。
 */
@Data
public class EvaluationApplyMaterial {
    /** 主键 */
    private Long id;
    /** 所属申报项 id（evaluation_apply_item.id） */
    private Long applyItemId;
    /** 原始文件名或展示名 */
    private String fileName;
    /** 文件存储地址（如对象存储 key 或 URL） */
    private String fileUrl;
}
