package com.project.evaluation.aop;

import com.alibaba.fastjson2.JSON;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.SysOperationLogService;
import com.project.evaluation.utils.SecurityContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    @Autowired
    private SysOperationLogService sysOperationLogService;

    // 在 RestController 层进行环绕，记录操作日志，这个是对整个类生效的
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void inRestController() {}

    // 实际生效的范围是两个条件都成立
    @Around("inRestController() && execution(public * com.project.evaluation.controller..*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 执行原方法，得到返回结果
        Object result = pjp.proceed();

        // 只记录业务成功（Result.code == 0），并且只记录有 HttpServletRequest 的场景
        if (!(result instanceof Result<?> r)) return result;
        if (r.getCode() == null || r.getCode() != 0) return result;

        // 当前线程里绑定的、与这次 Web 请求相关的一组“请求属性”
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return result;
        HttpServletRequest request = attrs.getRequest();
        if (request == null) return result;

        // 不记录读操作，判断是否为读操作
        if (shouldSkip(request)) return result;

        // 获取当前用户ID
        Long userId;
        try {
            Integer uid = SecurityContextUtil.getCurrentUserId();
            if (uid == null) return result;
            userId = uid.longValue();
        } catch (Exception ignored) {
            return result;
        }

        // 获取操作类型和内容
        String operation = operationFromRequest(request, pjp);
        String content = buildContent(request, pjp.getArgs());
        String ipAddress = resolveIp(request);
        
        // 记录操作日志
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
        String ip = firstValidIp(
                request.getHeader("X-Forwarded-For"),
                request.getHeader("X-Real-IP"),
                request.getHeader("Proxy-Client-IP"),
                request.getHeader("WL-Proxy-Client-IP"),
                request.getRemoteAddr()
        );
        return normalizeIp(ip);
    }

    private String firstValidIp(String... candidates) {
        if (candidates == null) return "";
        for (String c : candidates) {
            if (!StringUtils.hasText(c)) continue;
            // X-Forwarded-For 可能是逗号分隔，取第一个非 unknown
            for (String part : c.split(",")) {
                String ip = part.trim();
                if (!StringUtils.hasText(ip)) continue;
                if ("unknown".equalsIgnoreCase(ip)) continue;
                return ip;
            }
        }
        return "";
    }

    private String normalizeIp(String ip) {
        if (!StringUtils.hasText(ip)) return ip;
        String v = ip.trim();
        if ("::1".equals(v) || "0:0:0:0:0:0:0:1".equals(v)) return "127.0.0.1";

        // 去除 IPv6 映射前缀，如 ::ffff:127.0.0.1
        if (v.startsWith("::ffff:")) {
            v = v.substring("::ffff:".length());
        }
        try {
            InetAddress addr = InetAddress.getByName(v);
            if (addr.isLoopbackAddress()) return "127.0.0.1";
            return addr.getHostAddress();
        } catch (Exception e) {
            // 解析失败保留原值
            return v;
        }
    }

    private String buildContent(HttpServletRequest request, Object[] args) {
        String action = actionByMethod(request.getMethod());
        String resource = resourceByUri(request.getRequestURI());
        List<String> kv = extractImportantArgs(args);

        StringBuilder sb = new StringBuilder();
        sb.append(action).append(resource);
        if (!kv.isEmpty()) {
            sb.append("：").append(String.join("，", kv));
        }
        String content = sb.toString();
        return content.length() > 500 ? content.substring(0, 497) + "..." : content;
    }

    private String actionByMethod(String method) {
        if ("POST".equalsIgnoreCase(method)) return "新增";
        if ("PUT".equalsIgnoreCase(method)) return "更新";
        if ("DELETE".equalsIgnoreCase(method)) return "删除";
        return "操作";
    }

    private String resourceByUri(String uri) {
        if (!StringUtils.hasText(uri)) return "数据";
        if (uri.contains("/sys-student")) return "学生";
        if (uri.contains("/sys-class")) return "班级";
        if (uri.contains("/sys-college")) return "学院";
        if (uri.contains("/evaluation-rule")) return "规则总览";
        if (uri.contains("/ruleCategory-categories")) return "规则分类";
        if (uri.contains("/rule-items")) return "规则项";
        if (uri.contains("/sys-role")) return "角色";
        if (uri.contains("/user")) return "用户";
        return "数据";
    }

    private List<String> extractImportantArgs(Object[] args) {
        List<String> out = new ArrayList<>();
        if (args == null) return out;

        for (Object arg : args) {
            if (arg == null) continue;
            if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) continue;
            if (arg instanceof org.springframework.validation.BindingResult) continue;

            Class<?> c = arg.getClass();
            if (isSimpleValue(c)) {
                out.add(String.valueOf(arg));
                continue;
            }
            for (Field f : c.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) continue;
                String name = f.getName();
                if (!isImportantField(name)) continue;
                f.setAccessible(true);
                try {
                    Object v = f.get(arg);
                    if (v == null) continue;
                    out.add(name + "=" + safeValue(name, v));
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        return out.size() > 8 ? out.subList(0, 8) : out;
    }

    private boolean isSimpleValue(Class<?> c) {
        return c.isPrimitive()
                || Number.class.isAssignableFrom(c)
                || CharSequence.class.isAssignableFrom(c)
                || Boolean.class.isAssignableFrom(c);
    }

    private boolean isImportantField(String name) {
        if (!StringUtils.hasText(name)) return false;
        String n = name.toLowerCase();
        return n.endsWith("id")
                || n.contains("name")
                || n.contains("code")
                || n.contains("status")
                || n.contains("type")
                || n.contains("score")
                || n.contains("level")
                || n.contains("student");
    }

    private String safeValue(String fieldName, Object v) {
        String n = fieldName.toLowerCase();
        if (n.contains("password") || n.contains("pwd")) return "***";
        String s;
        try {
            s = JSON.toJSONString(v);
        } catch (Exception e) {
            s = String.valueOf(v);
        }
        s = s.replace("\"", "");
        return s.length() > 40 ? s.substring(0, 37) + "..." : s;
    }
}

