package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Role;
import com.project.evaluation.vo.Role.AddRoleReq;
import com.project.evaluation.vo.Role.UpdateRoleReq;

import java.util.List;

public interface RoleService {
    Role findRoleByName(String roleName);

    void addRole(AddRoleReq addRoleReq);

    void deleteRole(Integer id);

    Role findRoleById(Integer id);

    void updateRole(Integer id, UpdateRoleReq updateRoleReq);

    List<Role> roleList();

    PageBean<Role> paginationQuery(Integer pageNum, Integer pageSize, List<Integer> statuses);

    void assignPermissions(Integer roleId, List<Integer> permIds);

    List<Integer> getRolePermissions(Integer roleId);
}