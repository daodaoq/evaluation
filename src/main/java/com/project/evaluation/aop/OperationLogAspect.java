package com.project.evaluation.aop;

import com.alibaba.fastjson2.JSON;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.SysOperationLogService;
import com.project.evaluation.utils.SecurityContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void inRestController() {}

    @Around("inRestController() && execution(public * com.project.evaluation.controller..*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();

        // 只记录业务成功（Result.code == 0），并且只记录有 HttpServletRequest 的场景
        if (!(result instanceof Result<?> r)) return result;
        if (r.getCode() == null || r.getCode() != 0) return result;

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return result;
        HttpServletRequest request = attrs.getRequest();
        if (request == null) return result;

        if (shouldSkip(request)) return result;

        Long userId;
        try {
            Integer uid = SecurityContextUtil.getCurrentUserId();
            if (uid == null) return result;
            userId = uid.longValue();
        } catch (Exception ignored) {
            return result;
        }

        String operation = operationFromRequest(request, pjp);
        String content = buildContent(pjp.getArgs());
        String ipAddress = resolveIp(request);

        try {
            sysOperationLogService.record(userId, operation, content, ipAddress);
        } catch (Exception e) {
            // 日志写入失败不影响业务
            log.warn("写入操作日志失败: {}", e.getMessage());
        }

        return result;
    }

    private boolean shouldSkip(HttpServletRequest request) {
        // 仅记录写操作：避免频繁查询页面导致日志膨胀
        if ("GET".equalsIgnoreCase(request.getMethod())) return true;

        String uri = request.getRequestURI();
        if (!StringUtils.hasText(uri)) return false;
        return uri.contains("/login") || uri.contains("/logout");
    }

    private String operationFromRequest(HttpServletRequest request, ProceedingJoinPoint pjp) {
        String operation = request.getMethod() + " " + request.getRequestURI();
        if (!StringUtils.hasText(operation)) {
            operation = pjp.getSignature().getName();
        }
        operation = operation.trim();
        return operation.length() > 100 ? operation.substring(0, 97) + "..." : operation;
    }

    private String resolveIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip)) {
            // 可能形如：ip1, ip2
            String[] parts = ip.split(",");
            if (parts.length > 0 && StringUtils.hasText(parts[0])) return parts[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String buildContent(Object[] args) {
        String content;
        try {
            content = JSON.toJSONString(args);
        } catch (Exception e) {
            content = "args_serialization_failed";
        }
        content = maskSensitive(content);
        if (content == null) content = "";
        content = content.trim();
        return content.length() > 500 ? content.substring(0, 497) + "..." : content;
    }

    /**
     * 简单脱敏：对 JSON 字符串中常见密码字段进行替换。
     */
    private String maskSensitive(String json) {
        if (!StringUtils.hasText(json)) return json;

        // 覆盖常见字段：password / pwd / new_pwd / old_pwd / re_pwd
        json = json.replaceAll("(?i)\"(password|pwd|new_pwd|old_pwd|re_pwd)\"\\s*:\\s*\"[^\"]*\"", "\"$1\":\"***\"");
        // 如果后端 DTO 某些字段不是字符串（例如 number/bool），仍尽量脱敏
        json = json.replaceAll("(?i)\"(password|pwd|new_pwd|old_pwd|re_pwd)\"\\s*:\\s*[^,}\\]]+", "\"$1\":\"***\"");
        return json;
    }
}

