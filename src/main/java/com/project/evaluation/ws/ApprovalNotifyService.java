package com.project.evaluation.ws;

import com.project.evaluation.mapper.TeacherClassMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 管理教师端 WebSocket 连接，并按班级负责教师 + 在线管理员推送「待审申报」提醒。
 */
@Slf4j
@Service
public class ApprovalNotifyService {

    @Autowired
    private TeacherClassMapper teacherClassMapper;

    private final ConcurrentHashMap<Integer, CopyOnWriteArraySet<WebSocketSession>> sessionsByUserId = new ConcurrentHashMap<>();
    private final CopyOnWriteArraySet<WebSocketSession> adminSessions = new CopyOnWriteArraySet<>();

    public void registerSession(Integer userId, boolean isAdmin, WebSocketSession session) {
        if (userId == null || session == null) {
            return;
        }
        sessionsByUserId.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(session);
        if (isAdmin) {
            adminSessions.add(session);
        }
    }

    public void removeSession(Integer userId, boolean isAdmin, WebSocketSession session) {
        if (session == null) {
            return;
        }
        adminSessions.remove(session);
        if (userId != null) {
            CopyOnWriteArraySet<WebSocketSession> set = sessionsByUserId.get(userId);
            if (set != null) {
                set.remove(session);
                if (set.isEmpty()) {
                    sessionsByUserId.remove(userId, set);
                }
            }
        }
    }

    /**
     * 学生所在班级负责教师 + 当前在线的管理员（未重复推同一会话）
     */
    public void notifyNewApplyPendingReview(Integer classId, String jsonPayload) {
        if (jsonPayload == null) {
            return;
        }
        Set<WebSocketSession> sent = new HashSet<>();
        if (classId != null) {
            List<Integer> teacherIds = teacherClassMapper.selectTeacherUserIdsByClassId(classId);
            if (teacherIds != null) {
                for (Integer tid : teacherIds) {
                    CopyOnWriteArraySet<WebSocketSession> set = sessionsByUserId.get(tid);
                    if (set == null) {
                        continue;
                    }
                    for (WebSocketSession s : set) {
                        if (sendQuietly(s, jsonPayload)) {
                            sent.add(s);
                        }
                    }
                }
            }
        }
        for (WebSocketSession s : adminSessions) {
            if (sent.contains(s)) {
                continue;
            }
            sendQuietly(s, jsonPayload);
        }
    }

    private boolean sendQuietly(WebSocketSession session, String text) {
        if (session == null || !session.isOpen()) {
            return false;
        }
        try {
            session.sendMessage(new TextMessage(text));
            return true;
        } catch (IOException e) {
            log.debug("WebSocket 推送失败: {}", e.getMessage());
            return false;
        }
    }
}
