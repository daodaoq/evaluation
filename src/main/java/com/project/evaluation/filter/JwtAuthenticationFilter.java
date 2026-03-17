package com.project.evaluation.filter;

import com.project.evaluation.entity.MyUserDetails;
import com.project.evaluation.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 放行 /login 的请求
        if ("/user/login".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 获取请求头中的 token
        // 1. 获取Authorization请求头
        String authHeader = request.getHeader("Authorization");

        // 2. 校验Authorization头是否为空/是否以Bearer开头
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new BadCredentialsException("token缺失或格式错误（需以Bearer开头）");
        }

        // 3. 剥离Bearer前缀，提取纯Token字符串
        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            throw new BadCredentialsException("token为空（Bearer后无内容）");
        }

        // 获取 token 中的用户标识（注意解析的异常处理）
        String subject = "";
        try {
            Claims claims = JwtUtil.parseToken(token);
            subject = claims.getSubject();
        } catch (Exception e) {
            logger.error("token过期：{}");
            throw new BadCredentialsException("token错误");
        }

        // redis 中通过用户标识获取用户的认证信息（注意 redis 返回 null 的处理）
        MyUserDetails userDetails = (MyUserDetails) redisTemplate.opsForValue().get(subject);
        if (userDetails == null) {
            throw new BadCredentialsException("redis错误");
        }

        // 将用户信息存入安全上下文
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 过滤放行
        filterChain.doFilter(request, response);
    }
}
