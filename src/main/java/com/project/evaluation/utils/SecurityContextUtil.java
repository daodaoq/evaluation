package com.project.evaluation.utils;

import com.project.evaluation.entity.MyUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security 上下文工具类
 */
public class SecurityContextUtil {

    /**
     * 获取当前登录用户的ID
     * @return 用户ID
     * @throws RuntimeException 未登录/获取失败时抛出
     */
    public static Integer getCurrentUserId() {
        // 1. 从上下文获取认证对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. 校验认证状态（未登录/认证失效时直接抛异常）
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录或认证已失效");
        }

        // 3. 解析自定义的 MyUserDetails，获取用户ID
        Object principal = authentication.getPrincipal();
        if (principal instanceof MyUserDetails) {
            return ((MyUserDetails) principal).getMyUser().getId();
        } else {
            throw new RuntimeException("获取用户信息失败，当前主体类型不匹配");
        }
    }
}