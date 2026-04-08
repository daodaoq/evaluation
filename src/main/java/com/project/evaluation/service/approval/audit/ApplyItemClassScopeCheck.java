package com.project.evaluation.service.approval.audit;

import com.project.evaluation.mapper.TeacherClassMapper;
import com.project.evaluation.mapper.UserMapper;
import com.project.evaluation.utils.SecurityContextUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 教师仅可审批自己负责班级的申报项；管理员不受班级范围限制。
 */
@Component
@Order(35)
public class ApplyItemClassScopeCheck implements ApplyItemAuditCheck {

    private static final int TEACHER_ROLE_ID = 2;
    private static final int ADMIN_ROLE_ID = 3;

    private final UserMapper userMapper;
    private final TeacherClassMapper teacherClassMapper;

    public ApplyItemClassScopeCheck(UserMapper userMapper, TeacherClassMapper teacherClassMapper) {
        this.userMapper = userMapper;
        this.teacherClassMapper = teacherClassMapper;
    }

    @Override
    public void check(ApplyItemAuditContext context) {
        Integer currentUserId = SecurityContextUtil.getCurrentUserId();
        if (userMapper.countUserRole(currentUserId, ADMIN_ROLE_ID) > 0) {
            return;
        }
        if (userMapper.countUserRole(currentUserId, TEACHER_ROLE_ID) == 0) {
            throw new IllegalArgumentException("仅教师或管理员可审批");
        }
        Integer studentClassId = context.getStudentClassId();
        if (studentClassId == null || studentClassId <= 0) {
            throw new IllegalArgumentException("申报学生未绑定班级，无法审批");
        }
        List<Integer> scopeClassIds = teacherClassMapper.selectClassIdsByTeacherUserId(currentUserId);
        if (scopeClassIds == null || !scopeClassIds.contains(studentClassId)) {
            throw new IllegalArgumentException("只能审批自己负责班级的申报项");
        }
    }
}

