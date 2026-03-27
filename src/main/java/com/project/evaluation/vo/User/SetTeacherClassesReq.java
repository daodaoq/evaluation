package com.project.evaluation.vo.User;

import lombok.Data;

import java.util.List;

@Data
public class SetTeacherClassesReq {
    /** 负责班级 ID 列表，全量覆盖 */
    private List<Integer> classIds;
}
