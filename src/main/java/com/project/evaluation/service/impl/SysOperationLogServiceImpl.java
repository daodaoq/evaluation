package com.project.evaluation.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.MyUser;
import com.project.evaluation.entity.SysOperationLog;
import com.project.evaluation.mapper.SysOperationLogMapper;
import com.project.evaluation.mapper.UserMapper;
import com.project.evaluation.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SysOperationLogServiceImpl implements SysOperationLogService {

    @Autowired
    private SysOperationLogMapper sysOperationLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void record(Long userId, String operation, String content, String ipAddress) {
        sysOperationLogMapper.insert(userId, operation, content, ipAddress);
    }

    @Override
    public PageBean<SysOperationLog> pageLogs(Integer pageNum, Integer pageSize, String studentId, String keyword) {
        PageBean<SysOperationLog> pb = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);

        Long uid = null;
        if (StringUtils.hasText(studentId)) {
            MyUser u = userMapper.selectByUsername(studentId.trim());
            if (u == null || u.getId() == null) {
                pb.setTotal(0L);
                pb.setItems(java.util.List.of());
                return pb;
            }
            uid = u.getId().longValue();
        }

        var list = sysOperationLogMapper.selectOperationLogPage(uid, keyword);
        // 不保留 Page 局部变量，避免静态分析误判为资源泄露
        pb.setTotal(((Page<SysOperationLog>) list).getTotal());
        pb.setItems(((Page<SysOperationLog>) list).getResult());
        return pb;
    }
}

