package com.project.evaluation.vo.College;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 删除学院请求体
 */
@Data
public class DeleteCollegeReq {
    /**
     * 学院ID
     */
    @NotNull(message = "学院ID不能为空")
    @Positive(message = "学院ID必须为正数")
    private  Integer id;
}
