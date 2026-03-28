package com.project.evaluation.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 教师端订阅：仅维持连接，业务推送由 {@link ApprovalNotifyService} 完成。
 */
@Slf4j
@Component
public class ApprovalNotifyWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ApprovalNotifyService approvalNotifyService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Integer userId = (Integer) session.getAttributes().get(WsAuthHandshakeInterceptor.ATTR_USER_ID);
        Boolean isAdmin = (Boolean) session.getAttributes().get(WsAuthHandshakeInterceptor.ATTR_IS_ADMIN);
        approvalNotifyService.registerSession(userId, Boolean.TRUE.equals(isAdmin), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 客户端可发 ping，可选处理；当前忽略即可
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Integer userId = (Integer) session.getAttributes().get(WsAuthHandshakeInterceptor.ATTR_USER_ID);
        Boolean isAdmin = (Boolean) session.getAttributes().get(WsAuthHandshakeInterceptor.ATTR_IS_ADMIN);
        approvalNotifyService.removeSession(userId, Boolean.TRUE.equals(isAdmin), session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.debug("WebSocket 传输异常: {}", exception.getMessage());
        Integer userId = (Integer) session.getAttributes().get(WsAuthHandshakeInterceptor.ATTR_USER_ID);
        Boolean isAdmin = (Boolean) session.getAttributes().get(WsAuthHandshakeInterceptor.ATTR_IS_ADMIN);
        approvalNotifyService.removeSession(userId, Boolean.TRUE.equals(isAdmin), session);
    }
}
