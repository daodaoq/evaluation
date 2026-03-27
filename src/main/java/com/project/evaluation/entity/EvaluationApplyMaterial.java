package com.project.evaluation.entity;

import lombok.Data;

@Data
public class EvaluationApplyMaterial {
    private Long id;
    private Long applyItemId;
    private String fileName;
    private String fileUrl;
}
