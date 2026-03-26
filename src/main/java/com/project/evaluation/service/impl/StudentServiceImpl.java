package com.project.evaluation.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.project.evaluation.entity.MyUser;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.mapper.UserMapper;
import com.project.evaluation.service.StudentService;
import com.project.evaluation.vo.User.LoginUserVO;
import com.project.evaluation.vo.Student.AddStudentReq;
import com.project.evaluation.vo.Student.UpdateStudentReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {

    /** 与种子数据一致：sys_role.id = 1，role_key = STUDENT */
    private static final int STUDENT_ROLE_ID = 1;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public PageBean<LoginUserVO> pageStudents(Integer pageNum, Integer pageSize, String studentId, Integer status) {
        PageBean<LoginUserVO> pb = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);
        List<LoginUserVO> list = userMapper.selectStudentPage(studentId, status, STUDENT_ROLE_ID);
        Page<LoginUserVO> page = (Page<LoginUserVO>) list;
        pb.setTotal(page.getTotal());
        pb.setItems(page.getResult());
        return pb;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStudent(AddStudentReq req) {
        if (!StringUtils.hasText(req.getStudentId()) || !StringUtils.hasText(req.getPassword())
                || !StringUtils.hasText(req.getRealName())) {
            throw new IllegalArgumentException("学号、密码、姓名不能为空");
        }
        if (userMapper.countByStudentId(req.getStudentId().trim()) > 0) {
            throw new IllegalArgumentException("该学号已存在");
        }
        MyUser u = new MyUser();
        u.setStudentId(req.getStudentId().trim());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRealName(req.getRealName().trim());
        u.setCollegeId(req.getCollegeId());
        u.setClassId(req.getClassId());
        u.setStatus(req.getStatus() != null ? req.getStatus() : 1);
        userMapper.insertUser(u);
        if (u.getId() == null) {
            throw new IllegalStateException("创建用户失败");
        }
        userMapper.addUserRole(u.getId(), STUDENT_ROLE_ID);
        log.info("新增学生账号: id={}, studentId={}", u.getId(), u.getStudentId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStudent(Integer id, UpdateStudentReq req) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("非法用户ID");
        }
        if (userMapper.countUserRole(id, STUDENT_ROLE_ID) == 0) {
            throw new IllegalArgumentException("该用户不是学生或不存在");
        }
        MyUser u = userMapper.selectById(id);
        if (u == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (StringUtils.hasText(req.getStudentId())) {
            String sid = req.getStudentId().trim();
            if (!sid.equals(u.getStudentId()) && userMapper.countByStudentIdExcludeId(sid, id) > 0) {
                throw new IllegalArgumentException("该学号已被占用");
            }
            u.setStudentId(sid);
        }
        if (StringUtils.hasText(req.getPassword())) {
            u.setPassword(passwordEncoder.encode(req.getPassword()));
        } else {
            u.setPassword(null);
        }
        if (req.getRealName() != null) {
            u.setRealName(req.getRealName().trim());
        }
        u.setCollegeId(req.getCollegeId());
        u.setClassId(req.getClassId());
        if (req.getStatus() != null) {
            u.setStatus(req.getStatus());
        }
        userMapper.updateUserSelective(u);
        log.info("更新学生: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudent(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("非法用户ID");
        }
        if (userMapper.countUserRole(id, STUDENT_ROLE_ID) == 0) {
            throw new IllegalArgumentException("该用户不是学生或不存在");
        }
        userMapper.deleteUserRoles(id);
        userMapper.deleteUserById(id);
        log.info("删除学生账号: id={}", id);
    }
}
