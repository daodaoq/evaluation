package com.project.evaluation.vo.User;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Data
public class SetTeacherClassesReq {
    /** 负责班级 ID 列表，全量覆盖 */
    @NotNull(message = "班级列表不能为空")
    private List<Integer> classIds;
}
