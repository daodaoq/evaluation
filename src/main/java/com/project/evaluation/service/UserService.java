package com.project.evaluation.service;

import com.project.evaluation.entity.Authority;
import com.project.evaluation.entity.College;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.entity.SysPermission;
import com.project.evaluation.vo.User.AddTeacherReq;
import com.project.evaluation.vo.User.LoginReq;
import com.project.evaluation.vo.User.LoginResp;
import com.project.evaluation.vo.User.LoginUserVO;
import com.project.evaluation.vo.User.UpdateTeacherReq;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    Result<LoginResp> checkLogin(LoginReq loginReq);

    /**
     * 用户管理分页（不含密码）
     */
    PageBean<LoginUserVO> paginationQueryUsers(Integer pageNum, Integer pageSize, String studentId,
                                               List<Integer> statuses, List<Integer> collegeIds);

    Result<?> logout();

    Result<List<Authority>> getUserAuthority();

    /**
     * 当前登录用户在 sys_permission 中的权限行（扁平，与前端菜单/路由一致）
     */
    Result<List<SysPermission>> getUserPermissions();

    void assignRoles(Integer userId, List<Integer> roleIds);

    List<Integer> getUserRoles(Integer userId);

    int importTeachersByExcel(MultipartFile file);

    int batchAssignSameRole(List<Integer> userIds, Integer roleId);

    void addTeacher(AddTeacherReq req);

    void updateTeacher(Integer id, UpdateTeacherReq req);

    void deleteTeacher(Integer id);

    List<College> listColleges();

    /** 教师负责的班级 ID 列表（仅管理员） */
    List<Integer> getTeacherClassIds(Integer teacherUserId);

    /** 全量设置教师负责班级（仅管理员） */
    void setTeacherClasses(Integer teacherUserId, List<Integer> classIds);
}
