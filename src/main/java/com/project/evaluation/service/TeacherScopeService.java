package com.project.evaluation.service;

import com.project.evaluation.entity.MyUser;
import com.project.evaluation.mapper.TeacherClassMapper;
import com.project.evaluation.mapper.UserMapper;
import com.project.evaluation.utils.SecurityContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 教师数据范围：仅可管理所负责班级内的学生。
 */
@Service
public class TeacherScopeService {

    private static final int ADMIN_ROLE_ID = 3;
    private static final int TEACHER_ROLE_ID = 2;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TeacherClassMapper teacherClassMapper;

    public enum StudentMenuScope {
        ADMIN,
        TEACHER,
        DENIED
    }

    public StudentMenuScope resolveStudentMenuScope() {
        Integer uid = SecurityContextUtil.getCurrentUserId();
        List<Integer> roles = userMapper.getUserRoles(uid);
        if (roles == null || roles.isEmpty()) {
            return StudentMenuScope.DENIED;
        }
        if (roles.contains(ADMIN_ROLE_ID)) {
            return StudentMenuScope.ADMIN;
        }
        if (roles.contains(TEACHER_ROLE_ID)) {
            return StudentMenuScope.TEACHER;
        }
        return StudentMenuScope.DENIED;
    }

    public List<Integer> getManagedClassIdsForCurrentTeacher() {
        return teacherClassMapper.selectClassIdsByTeacherUserId(SecurityContextUtil.getCurrentUserId());
    }

    public void assertCanOperateStudentUser(Integer studentUserId) {
        StudentMenuScope scope = resolveStudentMenuScope();
        if (scope == StudentMenuScope.ADMIN) {
            return;
        }
        if (scope == StudentMenuScope.DENIED) {
            throw new AccessDeniedException("无权限");
        }
        MyUser u = userMapper.selectById(studentUserId);
        if (u == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (u.getClassId() == null) {
            throw new IllegalArgumentException("该学生未分班，您无权操作");
        }
        List<Integer> managed = getManagedClassIdsForCurrentTeacher();
        if (managed == null || managed.isEmpty() || !managed.contains(u.getClassId())) {
            throw new IllegalArgumentException("只能操作您负责班级内的学生");
        }
    }

    public void assertCanSetStudentClass(Integer classId) {
        StudentMenuScope scope = resolveStudentMenuScope();
        if (scope == StudentMenuScope.ADMIN) {
            return;
        }
        if (scope == StudentMenuScope.DENIED) {
            throw new AccessDeniedException("无权限");
        }
        if (classId == null) {
            throw new IllegalArgumentException("请选择班级");
        }
        List<Integer> managed = getManagedClassIdsForCurrentTeacher();
        if (managed == null || managed.isEmpty() || !managed.contains(classId)) {
            throw new IllegalArgumentException("只能将学生归入您负责的班级");
        }
    }
}
