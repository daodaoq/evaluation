package com.project.evaluation.service.impl;

import com.project.evaluation.entity.SysPermission;
import com.project.evaluation.mapper.SysPermissionMapper;
import com.project.evaluation.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysPermissionServiceImpl implements SysPermissionService {

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public List<SysPermission> listAllEnabled() {
        return sysPermissionMapper.selectAllEnabled();
    }

    @Override
    public List<SysPermission> listMenuEnabled() {
        return sysPermissionMapper.selectMenuPermsEnabled();
    }
}
