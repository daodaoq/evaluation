package com.project.evaluation.vo.Class;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import  lombok.Data;

/**
 * 添加班级请求体
 */
@Data
public class AddClassReq {

    /**
     * 班级名称
     */
    @NotBlank(message = "班级名称不能为空")
    @Size(max = 100, message = "班级名称长度不能超过100")
    private String className;

    /**
     * 学院ID
     */
    @NotNull(message = "学院ID不能为空")
    @Positive(message = "学院ID必须为正数")
    private Integer collegeId;

    /**
     * 年级
     */
    @NotNull(message = "年级不能为空")
    private Integer gradeYear;

}
