package com.project.evaluation.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.mapper.EvaluationApprovalMapper;
import com.project.evaluation.service.EvaluationApprovalService;
import com.project.evaluation.service.PeriodEventLogService;
import com.project.evaluation.service.approval.action.ApplyItemActionHandlerFactory;
import com.project.evaluation.service.approval.audit.ApplyItemAuditAction;
import com.project.evaluation.service.approval.audit.ApplyItemAuditCheck;
import com.project.evaluation.service.approval.audit.ApplyItemAuditContext;
import com.project.evaluation.utils.ApplyItemScoreUtil;
import com.project.evaluation.utils.MinioUtil;
import com.project.evaluation.utils.SecurityContextUtil;
import com.project.evaluation.vo.EvaluationApproval.ApplyItemMaterialJoinVO;
import com.project.evaluation.vo.EvaluationApproval.ApplyItemScoringSnapshot;
import com.project.evaluation.vo.EvaluationApproval.EvaluationApplyItemVO;
import com.project.evaluation.vo.EvaluationApproval.EvaluationApplyMaterialVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class EvaluationApprovalServiceImpl implements EvaluationApprovalService {

    @Autowired
    private EvaluationApprovalMapper evaluationApprovalMapper;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private List<ApplyItemAuditCheck> applyItemAuditChecks;

    @Autowired
    private ApplyItemActionHandlerFactory applyItemActionHandlerFactory;

    @Autowired
    private PeriodEventLogService periodEventLogService;

    @Override
    public PageBean<EvaluationApplyItemVO> pageApplyItems(Integer pageNum, Integer pageSize,
                                                          String studentNo, List<Long> periodIds,
                                                          List<String> applyStatuses, List<String> itemStatuses,
                                                          Long collegeId, Long classId) {
        try (Page<Object> ignored = PageHelper.startPage(pageNum, pageSize)) {
            List<EvaluationApplyItemVO> rows = evaluationApprovalMapper.pageApplyItems(
                    studentNo, periodIds, applyStatuses, itemStatuses, collegeId, classId
            );
            attachMaterials(rows);
            enrichDisplayScores(rows);
            PageInfo<EvaluationApplyItemVO> pageInfo = new PageInfo<>(rows);
            return new PageBean<>(pageInfo.getTotal(), pageInfo.getList());
        }
    }

    /**
     * 待审时库中细则项 score 常为 0，与审批通过时写入逻辑一致计算展示分（任职分/非细则等已入库的分值保持不变）。
     */
    private void enrichDisplayScores(List<EvaluationApplyItemVO> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        List<Long> ids = rows.stream()
                .map(EvaluationApplyItemVO::getApplyItemId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (ids.isEmpty()) {
            return;
        }
        List<ApplyItemScoringSnapshot> snaps = evaluationApprovalMapper.selectScoringSnapshotsByApplyItemIds(ids);
        Map<Long, ApplyItemScoringSnapshot> byId = snaps.stream()
                .filter(s -> s.getApplyItemId() != null)
                .collect(Collectors.toMap(ApplyItemScoringSnapshot::getApplyItemId, s -> s, (a, b) -> a));
        for (EvaluationApplyItemVO row : rows) {
            if (row.getApplyItemId() == null) {
                continue;
            }
            ApplyItemScoringSnapshot snap = byId.get(row.getApplyItemId());
            if (snap == null) {
                continue;
            }
            BigDecimal persisted = snap.getPersistedScore() != null ? snap.getPersistedScore() : BigDecimal.ZERO;
            BigDecimal display = ApplyItemScoreUtil.effectiveScore(
                    persisted,
                    snap.getSourceType(),
                    snap.getBaseScore(),
                    snap.getCoeff(),
                    snap.getScoreMode());
            row.setScore(display.setScale(2, RoundingMode.HALF_UP));
        }
    }

    private void attachMaterials(List<EvaluationApplyItemVO> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        List<Long> ids = rows.stream()
                .map(EvaluationApplyItemVO::getApplyItemId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (ids.isEmpty()) {
            return;
        }
        List<ApplyItemMaterialJoinVO> joins = evaluationApprovalMapper.listMaterialsByApplyItemIds(ids);
        Map<Long, List<EvaluationApplyMaterialVO>> byItem = new HashMap<>();
        for (ApplyItemMaterialJoinVO j : joins) {
            if (j.getApplyItemId() == null) {
                continue;
            }
            EvaluationApplyMaterialVO m = new EvaluationApplyMaterialVO();
            m.setFileName(j.getFileName());
            m.setFileUrl(j.getFileUrl());
            byItem.computeIfAbsent(j.getApplyItemId(), k -> new ArrayList<>()).add(m);
        }
        for (EvaluationApplyItemVO r : rows) {
            r.setMaterials(byItem.getOrDefault(r.getApplyItemId(), List.of()));
        }
    }

    @Override
    public String buildMaterialPreviewUrlForAuditor(String fileUrlOrKey) {
        if (!StringUtils.hasText(fileUrlOrKey)) {
            throw new IllegalArgumentException("材料参数不能为空");
        }
        String k = fileUrlOrKey.trim();
        if (evaluationApprovalMapper.countMaterialByFileUrl(k) < 1) {
            throw new IllegalArgumentException("材料不存在或已删除");
        }
        if (k.startsWith("http://") || k.startsWith("https://")) {
            return k;
        }
        if (!minioUtil.objectExists(k)) {
            throw new IllegalArgumentException("文件已失效或不存在");
        }
        return minioUtil.getPreviewUrl(k, 60, TimeUnit.MINUTES);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveApplyItem(Long applyItemId, String remark) {
        executeAction(applyItemId, remark, ApplyItemAuditAction.APPROVE);
        refreshApplyStatus(applyItemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectApplyItem(Long applyItemId, String remark) {
        executeAction(applyItemId, remark, ApplyItemAuditAction.REJECT);
        refreshApplyStatus(applyItemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reopenApplyItem(Long applyItemId) {
        executeAction(applyItemId, null, ApplyItemAuditAction.REOPEN);
        refreshApplyStatus(applyItemId);
    }

    private void refreshApplyStatus(Long applyItemId) {
        Long applyId = evaluationApprovalMapper.findApplyIdByApplyItemId(applyItemId);
        if (applyId == null) {
            return;
        }
        int total = evaluationApprovalMapper.countApplyItems(applyId);
        if (total <= 0) {
            return;
        }
        int rejected = evaluationApprovalMapper.countApplyItemsByStatus(applyId, "REJECTED");
        if (rejected > 0) {
            evaluationApprovalMapper.updateApplyStatus(applyId, "REJECTED");
            return;
        }
        int approved = evaluationApprovalMapper.countApplyItemsByStatus(applyId, "APPROVED");
        if (approved == total) {
            evaluationApprovalMapper.updateApplyStatus(applyId, "APPROVED");
        } else {
            evaluationApprovalMapper.updateApplyStatus(applyId, "SUBMITTED");
        }
    }

    private ApplyItemAuditContext runAuditChecks(Long applyItemId, ApplyItemAuditAction action) {
        ApplyItemAuditContext context = new ApplyItemAuditContext();
        context.setApplyItemId(applyItemId);
        context.setAction(action);
        for (ApplyItemAuditCheck check : applyItemAuditChecks) {
            check.check(context);
        }
        return context;
    }

    private void executeAction(Long applyItemId, String remark, ApplyItemAuditAction action) {
        ApplyItemAuditContext context = runAuditChecks(applyItemId, action);
        String beforeStatus = context.getItemStatus();
        applyItemActionHandlerFactory.get(action).handle(applyItemId, remark);
        String afterStatus = evaluationApprovalMapper.findApplyItemStatusById(applyItemId);
        logApprovalAction(context, beforeStatus, afterStatus, remark);
    }

    private void logApprovalAction(
            ApplyItemAuditContext context, String beforeStatus, String afterStatus, String remark) {
        Long periodId = context.getPeriodId();
        if (periodId == null) {
            return;
        }
        Integer operatorUserId;
        try {
            operatorUserId = SecurityContextUtil.getCurrentUserId();
        } catch (Exception e) {
            operatorUserId = null;
        }
        JSONObject detail = new JSONObject();
        detail.put("eventType", "APPROVAL_ACTION");
        detail.put("action", context.getAction() == null ? null : context.getAction().name());
        detail.put("applyItemId", context.getApplyItemId());
        detail.put("periodId", periodId);
        detail.put("operatorUserId", operatorUserId);
        detail.put("beforeStatus", beforeStatus);
        detail.put("afterStatus", afterStatus);
        detail.put("remark", remark);
        periodEventLogService.log(periodId, "APPROVAL_ACTION", detail.toJSONString());
    }
}
