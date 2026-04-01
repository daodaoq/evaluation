package com.project.evaluation.service.impl;

import com.project.evaluation.entity.EvaluationPublicity;
import com.project.evaluation.entity.MyUser;
import com.project.evaluation.entity.Time;
import com.project.evaluation.mapper.EvaluationPublicityMapper;
import com.project.evaluation.mapper.UserMapper;
import com.project.evaluation.service.EvaluationPublicityService;
import com.project.evaluation.service.PeriodEventLogService;
import com.project.evaluation.service.PeriodWorkflowService;
import com.project.evaluation.utils.SecurityContextUtil;
import com.project.evaluation.vo.Publicity.PublicitySaveReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class EvaluationPublicityServiceImpl implements EvaluationPublicityService {

    @Autowired
    private EvaluationPublicityMapper evaluationPublicityMapper;

    @Autowired
    private PeriodWorkflowService periodWorkflowService;

    @Autowired
    private PeriodEventLogService periodEventLogService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<EvaluationPublicity> listByPeriod(Long periodId) {
        periodWorkflowService.requirePeriod(periodId);
        return evaluationPublicityMapper.listByPeriod(periodId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PublicitySaveReq req) {
        if (req == null || req.getPeriodId() == null || req.getPeriodId() <= 0) {
            throw new IllegalArgumentException("请选择综测周期");
        }
        if (req.getStartTime() == null || req.getEndTime() == null) {
            throw new IllegalArgumentException("请填写公示开始与结束时间");
        }
        if (!req.getStartTime().isBefore(req.getEndTime())) {
            throw new IllegalArgumentException("公示结束时间须晚于开始时间");
        }
        periodWorkflowService.assertNotArchivedOnly(req.getPeriodId());
        String status = StringUtils.hasText(req.getStatus()) ? req.getStatus().trim().toUpperCase() : "OPEN";
        if (!"OPEN".equals(status) && !"CLOSED".equals(status)) {
            throw new IllegalArgumentException("状态须为 OPEN 或 CLOSED");
        }
        EvaluationPublicity row = new EvaluationPublicity();
        row.setPeriodId(req.getPeriodId());
        row.setClassId(req.getClassId());
        row.setStartTime(req.getStartTime());
        row.setEndTime(req.getEndTime());
        row.setStatus(status);
        evaluationPublicityMapper.insert(row);
        periodEventLogService.log(req.getPeriodId(), "PUBLICITY_ADD", "新增公示 id=" + row.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PublicitySaveReq req) {
        if (req == null || req.getId() == null || req.getId() <= 0) {
            throw new IllegalArgumentException("非法公示ID");
        }
        EvaluationPublicity exists = evaluationPublicityMapper.selectById(req.getId());
        if (exists == null) {
            throw new IllegalArgumentException("公示记录不存在");
        }
        periodWorkflowService.assertNotArchivedOnly(exists.getPeriodId());
        if (req.getPeriodId() == null || req.getPeriodId() <= 0) {
            throw new IllegalArgumentException("请选择综测周期");
        }
        if (req.getStartTime() == null || req.getEndTime() == null) {
            throw new IllegalArgumentException("请填写公示开始与结束时间");
        }
        if (!req.getStartTime().isBefore(req.getEndTime())) {
            throw new IllegalArgumentException("公示结束时间须晚于开始时间");
        }
        String status = StringUtils.hasText(req.getStatus()) ? req.getStatus().trim().toUpperCase() : "OPEN";
        if (!"OPEN".equals(status) && !"CLOSED".equals(status)) {
            throw new IllegalArgumentException("状态须为 OPEN 或 CLOSED");
        }
        req.setStatus(status);
        evaluationPublicityMapper.updateById(req);
        periodEventLogService.log(req.getPeriodId(), "PUBLICITY_UPDATE", "更新公示 id=" + req.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("非法公示ID");
        }
        EvaluationPublicity exists = evaluationPublicityMapper.selectById(id);
        if (exists == null) {
            throw new IllegalArgumentException("公示记录不存在");
        }
        periodWorkflowService.assertNotArchivedOnly(exists.getPeriodId());
        if (evaluationPublicityMapper.deleteById(id) == 0) {
            throw new IllegalStateException("删除失败");
        }
        periodEventLogService.log(exists.getPeriodId(), "PUBLICITY_DELETE", "删除公示 id=" + id);
    }

    @Override
    public List<EvaluationPublicity> listActiveForCurrentStudent(Long periodId) {
        if (periodId == null || periodId <= 0) {
            throw new IllegalArgumentException("请选择有效综测周期");
        }
        Time period = periodWorkflowService.requirePeriod(periodId);
        Integer uid = SecurityContextUtil.getCurrentUserId();
        MyUser u = userMapper.selectById(uid);
        Integer classId = u != null ? u.getClassId() : null;
        List<EvaluationPublicity> fromTable = evaluationPublicityMapper.listActiveForStudent(periodId, classId);
        if (!fromTable.isEmpty()) {
            return fromTable;
        }
        // 未在「公示」中维护明细记录时，若当前时间在周期配置的公示窗口内，则按周期时间回退展示（与流程文案一致）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pubS = period.getPublicNoticeStart();
        LocalDateTime pubE = period.getPublicNoticeEnd();
        if (pubS == null || pubE == null) {
            return fromTable;
        }
        if (now.isBefore(pubS) || now.isAfter(pubE)) {
            return fromTable;
        }
        EvaluationPublicity fallback = new EvaluationPublicity();
        fallback.setId(null);
        fallback.setPeriodId(periodId);
        fallback.setClassId(null);
        fallback.setStartTime(pubS);
        fallback.setEndTime(pubE);
        fallback.setStatus("OPEN");
        return Collections.singletonList(fallback);
    }
}
