package com.project.evaluation.mapper;

import com.project.evaluation.entity.MyUser;
import com.project.evaluation.vo.User.LoginUserVO;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper

public interface UserMapper {

    /**
     * 根据用户名查对象
     * @param username
     * @return
     */
    MyUser selectByUsername(@Param("username") String username);

    MyUser selectById(@Param("id") Integer id);

    /**
     * 用户管理分页（不含密码）
     */
    List<LoginUserVO> selectUserPage(@Param("studentId") String studentId, @Param("status") Integer status);

    /**
     * 仅含「学生」角色（sys_user_role.role_id = 学生）的分页列表
     */
    List<LoginUserVO> selectStudentPage(
            @Param("studentId") String studentId,
            @Param("status") Integer status,
            @Param("studentRoleId") Integer studentRoleId);

    int insertUser(MyUser user);

    int updateUserSelective(MyUser user);

    @Delete("DELETE FROM `sys_user` WHERE id = #{id}")
    void deleteUserById(@Param("id") Integer id);

    @Select("SELECT COUNT(1) FROM `sys_user` WHERE student_id = #{studentId}")
    int countByStudentId(@Param("studentId") String studentId);

    @Select("SELECT COUNT(1) FROM `sys_user` WHERE student_id = #{studentId} AND id != #{excludeId}")
    int countByStudentIdExcludeId(@Param("studentId") String studentId, @Param("excludeId") Integer excludeId);

    @Select("SELECT COUNT(1) FROM `sys_user_role` WHERE user_id = #{userId} AND role_id = #{roleId}")
    int countUserRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId);

    /**
     * 删除用户的所有角色
     * @param userId
     */
    @Delete("DELETE FROM `sys_user_role` WHERE user_id = #{userId}")
    void deleteUserRoles(Integer userId);

    /**
     * 为用户添加角色
     * @param userId
     * @param roleId
     */
    @Insert("INSERT INTO `sys_user_role` (user_id, role_id, create_time, update_time) VALUES (#{userId}, #{roleId}, NOW(), NOW())")
    void addUserRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId);

    /**
     * 获取用户的角色ID列表
     * @param userId
     * @return
     */
    @Select("SELECT role_id FROM `sys_user_role` WHERE user_id = #{userId}")
    List<Integer> getUserRoles(Integer userId);
}
