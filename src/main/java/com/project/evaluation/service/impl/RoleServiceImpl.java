package com.project.evaluation.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Role;
import com.project.evaluation.mapper.RoleMapper;
import com.project.evaluation.service.RoleService;
import com.project.evaluation.vo.Role.AddRoleReq;
import com.project.evaluation.vo.Role.UpdateRoleReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 通过角色名称查找角色
     * @param roleName
     * @return
     */
    @Override
    public Role findRoleByName(String roleName) {
        if(!StringUtils.hasText(roleName)){
            throw new IllegalArgumentException("角色名称不能为空");
        }
        return roleMapper.findRoleByName(roleName.trim());
    }

    /**
     * 添加角色
     * @param addRoleReq
     */
    @Override
    public void addRole(AddRoleReq addRoleReq) {
        roleMapper.addRole(addRoleReq);
        log.info("添加成功：{}", addRoleReq);
    }

    /**
     * 删除角色
     * @param id
     */
    @Override
    public void deleteRole(Integer id) {
        if(id == null || id <= 0){
            throw new IllegalArgumentException("非法角色ID");
        }
        int rows = roleMapper.deleteRole(id);
        if(rows == 0){
            log.warn("删除失败，角色id不存在：{}", id);
            throw new IllegalStateException("角色不存在或已删除");
        }
        log.info("删除角色成功：id={}", id);
    }

    /**
     * 通过id查找角色
     * @param id
     * @return
     */
    @Override
    public Role findRoleById(Integer id) {
        if(id <= 0 || id == null){
            throw new IllegalArgumentException("非法角色ID");
        }
        return roleMapper.findRoleById(id);
    }

    /**
     * 更新角色信息
     * @param id
     * @param updateRoleReq
     */
    @Override
    public void updateRole(Integer id, UpdateRoleReq updateRoleReq) {
        roleMapper.updateRole(id, updateRoleReq);
    }

    /**
     * 批量获取角色
     * @return
     */
    @Override
    public List<Role> roleList() {
        return roleMapper.roleList();
    }

    /**
     * 分页条件查询角色
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageBean<Role> paginationQuery(Integer pageNum, Integer pageSize, Integer status) {
        PageBean<Role> pb = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);
        List<Role> roles = roleMapper.paginationQuery(status);
        PageInfo<Role> info = new PageInfo<>(roles);
        pb.setTotal(info.getTotal());
        pb.setItems(info.getList());
        return pb;
    }

    @Override
    public void assignPermissions(Integer roleId, List<Integer> permIds) {
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("非法角色ID");
        }
        
        // 先删除角色的所有权限
        roleMapper.deleteRolePermissions(roleId);
        
        // 再添加新的权限
        if (permIds != null && !permIds.isEmpty()) {
            for (Integer permId : permIds) {
                roleMapper.addRolePermission(roleId, permId);
            }
        }
        
        log.info("角色权限分配成功：roleId={}, permIds={}", roleId, permIds);
    }

    @Override
    public List<Integer> getRolePermissions(Integer roleId) {
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("非法角色ID");
        }
        return roleMapper.getRolePermissions(roleId);
    }
}