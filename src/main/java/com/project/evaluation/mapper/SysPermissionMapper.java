package com.project.evaluation.mapper;

import com.project.evaluation.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysPermissionMapper {

    /**
     * 当前用户通过角色关联得到的权限（去重，仅启用）
     */
    List<SysPermission> selectByUserId(@Param("userId") Integer userId);

    /** 按主键查单条（用于补全菜单树父节点） */
    SysPermission selectById(@Param("id") Long id);

    /**
     * 全库启用中的权限（扁平列表，供树形组装）
     */
    List<SysPermission> selectAllEnabled();

    /**
     * 用于角色分配的「菜单类」权限（与项目 perm_code 约定为 *:menu 一致）
     */
    List<SysPermission> selectMenuPermsEnabled();
}
