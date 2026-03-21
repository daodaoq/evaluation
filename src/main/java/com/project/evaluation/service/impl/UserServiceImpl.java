package com.project.evaluation.service.impl;

import com.project.evaluation.entity.Authority;
import com.project.evaluation.entity.MyUserDetails;
import com.project.evaluation.entity.Result;
import com.project.evaluation.mapper.AuthorityMapper;
import com.project.evaluation.service.UserService;
import com.project.evaluation.utils.JwtUtil;
import com.project.evaluation.utils.SecurityContextUtil;
import com.project.evaluation.vo.User.LoginReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AuthorityMapper authorityMapper;

    /**
     * 用户登录
     * @param loginReq
     * @return
     */
    @Override
    public Result checkLogin(LoginReq loginReq) {
        // 得到用户名和密码参数
        String username = loginReq.getStudentId();
        String password = loginReq.getPassword();

        // 封装到一个待认证的 authRequest 对象中
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        try {
            // 发起认证并得到认证后的结果 authentication 对象
            Authentication authentication = authenticationManager.authenticate(authRequest);
            if (authentication.isAuthenticated()) {
                // 认证成功后办如下 4 件事：
                // 1、应注意 authentication 对象放入安全上下文供后续过滤器取用
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 2、生成 token
                String jwtKey = "user:" + ((MyUserDetails) authentication.getPrincipal()).getMyUser().getId();
                String token = JwtUtil.createToken(jwtKey, 1000 * 60 * 5);

                // 3、用户认证信息写入 redis 保存
                redisTemplate.opsForValue().set(jwtKey, authentication.getPrincipal(), 1000 * 60 * 5, TimeUnit.SECONDS);

                // 4、响应前端 JSON 数据
                return Result.success(token);
            }
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        }

        // 认证失败响应前端 JSON 数据
        return Result.error("登录失败");
    }

    /**
     * 用户登出
     * @return
     */
    @Override
    public Result logout() {
        // 清空 redis 信息
        String redisKey = "";
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        redisKey = "user:" + userDetails.getMyUser().getId();
        redisTemplate.delete(redisKey);
        // 清空安全上下文
        SecurityContextHolder.clearContext();
        // 返回数据
        return Result.success("登出成功");
    }

    /**
     * 获取用户权限
     * @return
     */
    @Override
    public Result<List<Authority>> getUserAuthority() {
        Integer userId = SecurityContextUtil.getCurrentUserId();
        List<Authority> authorities = authorityMapper.selectAllAuthorityDetailsByUserId(userId);
        return Result.success(authorities);
    }
}
