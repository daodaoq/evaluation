package com.project.evaluation.ws;

import com.project.evaluation.entity.MyUserDetails;
import com.project.evaluation.mapper.UserMapper;
import com.project.evaluation.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手：query 参数 token（与登录返回的 JWT 一致），并校验 Redis 登录态。
 * 仅教师或管理员可连接（与后台登录规则一致）。
 */
@Component
public class WsAuthHandshakeInterceptor implements HandshakeInterceptor {

    private static final int STUDENT_ROLE_ID = 1;
    private static final int TEACHER_ROLE_ID = 2;
    private static final int ADMIN_ROLE_ID = 3;

    public static final String ATTR_USER_ID = "wsUserId";
    public static final String ATTR_IS_ADMIN = "wsIsAdmin";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }
        String token = servletRequest.getServletRequest().getParameter("token");
        if (!StringUtils.hasText(token)) {
            return false;
        }
        String subject;
        try {
            Claims claims = JwtUtil.parseToken(token.trim());
            subject = claims.getSubject();
        } catch (Exception e) {
            return false;
        }
        if (!StringUtils.hasText(subject)) {
            return false;
        }
        MyUserDetails details = (MyUserDetails) redisTemplate.opsForValue().get(subject);
        if (details == null || details.getMyUser() == null || details.getMyUser().getId() == null) {
            return false;
        }
        Integer uid = details.getMyUser().getId();
        boolean hasStudent = userMapper.countUserRole(uid, STUDENT_ROLE_ID) > 0;
        boolean hasTeacher = userMapper.countUserRole(uid, TEACHER_ROLE_ID) > 0;
        boolean hasAdmin = userMapper.countUserRole(uid, ADMIN_ROLE_ID) > 0;
        if (hasStudent && !hasTeacher && !hasAdmin) {
            return false;
        }
        attributes.put(ATTR_USER_ID, uid);
        attributes.put(ATTR_IS_ADMIN, hasAdmin);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
