package com.project.evaluation.service;

import com.project.evaluation.mapper.PeriodEventLogMapper;
import com.project.evaluation.utils.SecurityContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PeriodEventLogService {

    @Autowired
    private PeriodEventLogMapper periodEventLogMapper;

    public void log(Long periodId, String eventCode, String detail) {
        if (periodId == null) {
            return;
        }
        try {
            Integer uid = SecurityContextUtil.getCurrentUserId();
            periodEventLogMapper.insert(periodId, uid, eventCode, detail);
        } catch (Exception e) {
            log.debug("周期事件日志写入失败: {}", e.getMessage());
        }
    }
}
