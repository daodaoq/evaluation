package com.project.evaluation.service;

import com.project.evaluation.entity.Time;
import com.project.evaluation.mapper.StudentPeriodConfirmMapper;
import com.project.evaluation.mapper.TimeMapper;
import com.project.evaluation.vo.StudentApply.StudentPeriodWorkflowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 综测周期阶段：申报窗口、审核截止、公示与异议、归档、学生确认锁定。
 */
@Service
public class PeriodWorkflowService {

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private TimeMapper timeMapper;

    @Autowired
    private StudentPeriodConfirmMapper studentPeriodConfirmMapper;

    public Time requirePeriod(Long periodId) {
        if (periodId == null || periodId <= 0) {
            throw new IllegalArgumentException("请选择综测周期");
        }
        Time t = timeMapper.findTimeById(periodId.intValue());
        if (t == null) {
            throw new IllegalArgumentException("综测周期不存在");
        }
        return t;
    }

    public boolean isArchived(Time t) {
        return t.getArchived() != null && t.getArchived() == 1;
    }

    public void assertNotArchived(Time t) {
        if (isArchived(t)) {
            throw new IllegalStateException("该综测周期已归档锁定，不可进行此操作");
        }
    }

    /** 申诉处理、退回待审等：仅禁止归档，不受审核截止时间限制 */
    public void assertNotArchivedOnly(Long periodId) {
        assertNotArchived(requirePeriod(periodId));
    }

    public LocalDateTime effectiveApplicationStart(Time t) {
        if (t.getApplicationStartTime() != null) {
            return t.getApplicationStartTime();
        }
        return parseLegacyDateTime(t.getStartTime());
    }

    public LocalDateTime effectiveApplicationEnd(Time t) {
        if (t.getApplicationEndTime() != null) {
            return t.getApplicationEndTime();
        }
        return parseLegacyDateTime(t.getEndTime());
    }

    /**
     * 学生提交新申报
     */
    public void assertStudentCanSubmit(Long periodId, Integer studentUserId) {
        Time t = requirePeriod(periodId);
        assertNotArchived(t);
        if (t.getStatus() == null || t.getStatus() != 1) {
            throw new IllegalStateException("当前综测周期未启用");
        }
        if (studentUserId != null && studentPeriodConfirmMapper.exists(studentUserId.longValue(), periodId)) {
            throw new IllegalStateException("您已确认本周期综测结果无异议，不可再提交申报");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = effectiveApplicationStart(t);
        LocalDateTime end = effectiveApplicationEnd(t);
        if (start != null && now.isBefore(start)) {
            throw new IllegalStateException("尚未到申报开放时间");
        }
        if (end != null && now.isAfter(end)) {
            throw new IllegalStateException("已超过申报截止时间");
        }
    }

    /**
     * 教师审核（通过/驳回）、处理申诉
     */
    public void assertTeacherCanAudit(Long periodId) {
        Time t = requirePeriod(periodId);
        assertNotArchived(t);
        LocalDateTime now = LocalDateTime.now();
        if (t.getReviewEndTime() != null && now.isAfter(t.getReviewEndTime())) {
            throw new IllegalStateException("已超过教师审核截止时间");
        }
    }

    /**
     * 学生发起申诉（针对已驳回项）
     */
    public void assertStudentCanAppeal(Long periodId, Integer studentUserId) {
        Time t = requirePeriod(periodId);
        assertNotArchived(t);
        if (studentUserId != null && studentPeriodConfirmMapper.exists(studentUserId.longValue(), periodId)) {
            throw new IllegalStateException("您已确认本周期无异议，不可再发起申诉");
        }
        LocalDateTime now = LocalDateTime.now();
        if (t.getReviewEndTime() != null && now.isAfter(t.getReviewEndTime())) {
            throw new IllegalStateException("已超过审核截止，不可再发起申诉");
        }
    }

    /**
     * 管理端维护智育分、导入等
     */
    public void assertAcademicMutable(Long periodId) {
        Time t = requirePeriod(periodId);
        assertNotArchived(t);
        LocalDateTime now = LocalDateTime.now();
        if (t.getReviewEndTime() != null && now.isAfter(t.getReviewEndTime())) {
            throw new IllegalStateException("已超过审核截止，不可再修改智育成绩");
        }
    }

    /**
     * 学生提交异议
     */
    public void assertStudentCanObject(Long periodId, Integer studentUserId) {
        Time t = requirePeriod(periodId);
        assertNotArchived(t);
        if (studentUserId != null && studentPeriodConfirmMapper.exists(studentUserId.longValue(), periodId)) {
            throw new IllegalStateException("您已确认无异议，不可再提交异议");
        }
        if (t.getPublicNoticeStart() == null || t.getPublicNoticeEnd() == null) {
            throw new IllegalStateException("当前周期未配置公示时间，无法提交异议");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(t.getPublicNoticeStart())) {
            throw new IllegalStateException("公示尚未开始");
        }
        LocalDateTime objEnd = t.getObjectionEndTime() != null ? t.getObjectionEndTime() : t.getPublicNoticeEnd();
        if (now.isAfter(objEnd)) {
            throw new IllegalStateException("已超过异议截止时间");
        }
    }

    /**
     * 学生确认「无异议」（锁定申报）
     */
    public void assertStudentCanConfirm(Long periodId) {
        Time t = requirePeriod(periodId);
        assertNotArchived(t);
        LocalDateTime now = LocalDateTime.now();
        if (t.getPublicNoticeStart() == null || now.isBefore(t.getPublicNoticeStart())) {
            throw new IllegalStateException("公示开始后方可确认无异议");
        }
        LocalDateTime objEnd = t.getObjectionEndTime() != null ? t.getObjectionEndTime() : t.getPublicNoticeEnd();
        if (objEnd != null && now.isAfter(objEnd)) {
            throw new IllegalStateException("已超过异议期，无法再确认");
        }
    }

    /**
     * 学生端展示：当前阶段说明（不抛业务异常，仅状态位）
     */
    public StudentPeriodWorkflowVO buildStudentWorkflowView(Long periodId, Integer studentUserId) {
        StudentPeriodWorkflowVO vo = new StudentPeriodWorkflowVO();
        vo.setPeriodId(periodId);
        Time t;
        try {
            t = requirePeriod(periodId);
        } catch (Exception e) {
            vo.setApplicationWindowHint("周期不存在");
            return vo;
        }
        vo.setPeriodName(t.getPeriodName());
        vo.setArchived(isArchived(t));
        if (studentUserId != null) {
            vo.setStudentConfirmed(studentPeriodConfirmMapper.exists(studentUserId.longValue(), periodId));
        } else {
            vo.setStudentConfirmed(false);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime appS = effectiveApplicationStart(t);
        LocalDateTime appE = effectiveApplicationEnd(t);
        boolean statusOk = t.getStatus() != null && t.getStatus() == 1;
        boolean appOpen = statusOk && !isArchived(t)
                && (appS == null || !now.isBefore(appS))
                && (appE == null || !now.isAfter(appE));
        vo.setApplicationWindowOpen(appOpen);
        if (!statusOk) {
            vo.setApplicationWindowHint("该综测周期未启用");
        } else if (isArchived(t)) {
            vo.setApplicationWindowHint("周期已归档");
        } else if (appS != null && now.isBefore(appS)) {
            vo.setApplicationWindowHint("尚未到申报开放时间");
        } else if (appE != null && now.isAfter(appE)) {
            vo.setApplicationWindowHint("已超过申报截止时间");
        } else {
            vo.setApplicationWindowHint("申报开放中");
        }

        boolean reviewEnded = t.getReviewEndTime() != null && now.isAfter(t.getReviewEndTime());
        vo.setReviewEnded(reviewEnded);

        boolean inPub = t.getPublicNoticeStart() != null && t.getPublicNoticeEnd() != null
                && !now.isBefore(t.getPublicNoticeStart())
                && !now.isAfter(t.getPublicNoticeEnd());
        vo.setInPublicNoticePhase(inPub);
        if (t.getPublicNoticeStart() == null) {
            vo.setPublicNoticeHint("未配置公示时间");
        } else if (now.isBefore(t.getPublicNoticeStart())) {
            vo.setPublicNoticeHint("公示尚未开始");
        } else if (t.getPublicNoticeEnd() != null && now.isAfter(t.getPublicNoticeEnd())) {
            vo.setPublicNoticeHint("公示已结束");
        } else {
            vo.setPublicNoticeHint("公示进行中");
        }

        LocalDateTime objEnd = t.getObjectionEndTime() != null ? t.getObjectionEndTime() : t.getPublicNoticeEnd();
        boolean inObj = t.getPublicNoticeStart() != null && objEnd != null
                && !now.isBefore(t.getPublicNoticeStart())
                && !now.isAfter(objEnd);
        vo.setInObjectionWindow(inObj);
        if (t.getPublicNoticeStart() == null) {
            vo.setObjectionHint("未配置公示，不可提交异议");
        } else if (now.isBefore(t.getPublicNoticeStart())) {
            vo.setObjectionHint("公示开始后方可提交异议");
        } else if (objEnd != null && now.isAfter(objEnd)) {
            vo.setObjectionHint("已超过异议截止时间");
        } else {
            vo.setObjectionHint("异议期内");
        }

        return vo;
    }

    public static LocalDateTime parseLegacyDateTime(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        String v = s.trim().replace('T', ' ');
        if (v.length() >= 19) {
            v = v.substring(0, 19);
        }
        try {
            return LocalDateTime.parse(v, DT);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(s.trim().replace('T', ' '), DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"));
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }
}
