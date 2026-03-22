package com.project.evaluation.mapper;

import com.project.evaluation.entity.MyUser;
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
