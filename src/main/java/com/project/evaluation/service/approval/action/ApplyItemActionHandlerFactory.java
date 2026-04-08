package com.project.evaluation.service.approval.action;

import com.project.evaluation.service.approval.audit.ApplyItemAuditAction;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 审批动作处理器工厂。
 */
@Component
public class ApplyItemActionHandlerFactory {

    private final Map<ApplyItemAuditAction, ApplyItemActionHandler> handlers = new EnumMap<>(ApplyItemAuditAction.class);

    public ApplyItemActionHandlerFactory(List<ApplyItemActionHandler> handlerList) {
        for (ApplyItemActionHandler handler : handlerList) {
            handlers.put(handler.action(), handler);
        }
    }

    public ApplyItemActionHandler get(ApplyItemAuditAction action) {
        ApplyItemActionHandler handler = handlers.get(action);
        if (handler == null) {
            throw new IllegalArgumentException("不支持的审批动作: " + action);
        }
        return handler;
    }
}

