package com.project.evaluation.mapper;

import com.project.evaluation.entity.Role;
import com.project.evaluation.vo.Role.AddRoleReq;
import com.project.evaluation.vo.Role.UpdateRoleReq;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 角色Mapper层
 */
@Mapper
public interface RoleMapper {

    /**
     * 添加角色
     * @param addRoleReq
     */
    @Insert("INSERT INTO `sys_role`" +
            "(role_name, role_code, description, status, create_time, update_time)" +
            "VALUES(#{roleName}, #{roleCode}, #{description}, #{status}, NOW(), NOW())")
    void addRole(AddRoleReq addRoleReq);

    /**
     * 删除角色
     * @param id
     * @return
     */
    @Delete("DELETE FROM `sys_role` WHERE id = #{id}")
    int deleteRole(Integer id);

    /**
     * 更新角色
     * @param id
     * @param updateRoleReq
     */
    @Update("UPDATE `sys_role` SET " +
            "role_name = #{updateRoleReq.roleName}," +
            "role_code = #{updateRoleReq.roleCode}," +
            "description = #{updateRoleReq.description}," +
            "status = #{updateRoleReq.status}," +
            "update_time = NOW() WHERE id = #{id}")
    void updateRole(Integer id, UpdateRoleReq updateRoleReq);

    /**
     * 通过名字查找角色
     * @param name
     * @return
     */
    @Select("SELECT * FROM `sys_role` WHERE role_name = #{name}")
    Role findRoleByName(String name);

    /**
     * 通过id查找角色
     * @param id
     * @return
     */
    @Select("SELECT * FROM `sys_role` WHERE id = #{id}")
    Role findRoleById(Integer id);

    /**
     * 批量获取角色列表
     * @return
     */
    @Select("SELECT * FROM `sys_role`")
    List<Role> roleList();

    /**
     * 分页条件查询角色
     * @param status
     * @return
     */
    List<Role> paginationQuery(Integer status);
}