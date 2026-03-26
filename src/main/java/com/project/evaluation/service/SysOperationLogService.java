package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.SysOperationLog;

public interface SysOperationLogService {
    void record(Long userId, String operation, String content, String ipAddress);

    PageBean<SysOperationLog> pageLogs(Integer pageNum, Integer pageSize, String studentId, String keyword);
}

