package com.project.evaluation.config;

import com.project.evaluation.ws.ApprovalNotifyWebSocketHandler;
import com.project.evaluation.ws.WsAuthHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ApprovalNotifyWebSocketHandler approvalNotifyWebSocketHandler;

    @Autowired
    private WsAuthHandshakeInterceptor wsAuthHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(approvalNotifyWebSocketHandler, "/ws/approval-notify")
                .addInterceptors(wsAuthHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }
}
