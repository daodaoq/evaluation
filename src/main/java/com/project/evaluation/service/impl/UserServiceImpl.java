package com.project.evaluation.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.project.evaluation.entity.Authority;
import com.project.evaluation.entity.MyUser;
import com.project.evaluation.entity.MyUserDetails;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.entity.SysPermission;
import com.project.evaluation.mapper.AuthorityMapper;
import com.project.evaluation.mapper.SysPermissionMapper;
import com.project.evaluation.mapper.UserMapper;
import com.project.evaluation.service.UserService;
import com.project.evaluation.utils.JwtUtil;
import com.project.evaluation.utils.SecurityContextUtil;
import com.project.evaluation.vo.User.LoginReq;
import com.project.evaluation.vo.User.LoginResp;
import com.project.evaluation.vo.User.LoginUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    /** 与 JWT、Redis 会话一致：24 小时 */
    private static final long LOGIN_TOKEN_TTL_MS = 1000L * 60 * 60 * 24;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     * @param loginReq
     * @return
     */
    @Override
    public Result<LoginResp> checkLogin(LoginReq loginReq) {
        String username = loginReq.getStudentId();
        String password = loginReq.getPassword();

        UsernamePasswordAuthenticationToken authRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        try {
            Authentication authentication = authenticationManager.authenticate(authRequest);
            if (!authentication.isAuthenticated()) {
                return Result.error("登录失败");
            }

            MyUserDetails principal = (MyUserDetails) authentication.getPrincipal();
            MyUser myUser = principal.getMyUser();
            String jwtKey = "user:" + myUser.getId();
            String token = JwtUtil.createToken(jwtKey, LOGIN_TOKEN_TTL_MS);

            redisTemplate.opsForValue().set(jwtKey, principal, LOGIN_TOKEN_TTL_MS, TimeUnit.MILLISECONDS);

            LoginUserVO userVo = toLoginUserVO(myUser);
            return Result.success(new LoginResp(token, userVo));
        } catch (BadCredentialsException e) {
            log.debug("登录失败：账号或密码错误, user={}", username);
            return Result.error("账号或密码错误");
        } catch (DisabledException e) {
            return Result.error("账号已禁用");
        } catch (AuthenticationException e) {
            log.warn("登录认证异常: {}", e.getMessage());
            return Result.error("登录失败");
        }
    }

    @Override
    public PageBean<LoginUserVO> paginationQueryUsers(Integer pageNum, Integer pageSize, String studentId, Integer status) {
        PageBean<LoginUserVO> pb = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);
        List<LoginUserVO> list = userMapper.selectUserPage(studentId, status);
        Page<LoginUserVO> page = (Page<LoginUserVO>) list;
        pb.setTotal(page.getTotal());
        pb.setItems(page.getResult());
        return pb;
    }

    private static LoginUserVO toLoginUserVO(MyUser u) {
        LoginUserVO vo = new LoginUserVO();
        if (u.getId() != null) {
            vo.setUserId(u.getId().longValue());
        }
        vo.setUserNo(u.getStudentId());
        vo.setUserName(u.getRealName());
        if (u.getCollegeId() != null) {
            vo.setCollegeId(u.getCollegeId().longValue());
        }
        if (u.getClassId() != null) {
            vo.setClassId(u.getClassId().longValue());
        }
        vo.setStatus(u.getStatus());
        vo.setCreateTime(u.getCreateTime());
        vo.setUpdateTime(u.getUpdateTime());
        return vo;
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

    @Override
    public Result<List<SysPermission>> getUserPermissions() {
        Integer userId = SecurityContextUtil.getCurrentUserId();
        List<SysPermission> list = sysPermissionMapper.selectByUserId(userId);
        return Result.success(fillPermissionAncestors(list));
    }

    /**
     * 侧栏树依赖 parent_id：若只分配了子菜单 perm，需把 sys_permission 中的父级一并加入列表，
     * 否则 listToTree 无法挂接（常见于「教学评估」分组下的菜单）。
     */
    private List<SysPermission> fillPermissionAncestors(List<SysPermission> list) {
        if (list == null || list.isEmpty()) {
            return list == null ? List.of() : list;
        }
        Map<Long, SysPermission> byId = new LinkedHashMap<>();
        for (SysPermission p : list) {
            if (p.getId() != null) {
                byId.putIfAbsent(p.getId(), p);
            }
        }
        ArrayDeque<Long> queue = new ArrayDeque<>(byId.keySet());
        while (!queue.isEmpty()) {
            Long id = queue.poll();
            SysPermission p = byId.get(id);
            if (p == null) {
                continue;
            }
            Long pid = p.getParentId();
            if (pid == null || pid <= 0 || byId.containsKey(pid)) {
                continue;
            }
            SysPermission parent = sysPermissionMapper.selectById(pid);
            if (parent != null && parent.getStatus() != null && parent.getStatus() == 1) {
                byId.put(parent.getId(), parent);
                queue.add(parent.getId());
            }
        }
        return new ArrayList<>(byId.values());
    }

    @Override
    public void assignRoles(Integer userId, List<Integer> roleIds) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("非法用户ID");
        }
        
        // 先删除用户的所有角色
        userMapper.deleteUserRoles(userId);
        
        // 再添加新的角色
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Integer roleId : roleIds) {
                userMapper.addUserRole(userId, roleId);
            }
        }
        
        log.info("用户角色分配成功：userId={}, roleIds={}", userId, roleIds);
    }

    @Override
    public List<Integer> getUserRoles(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("非法用户ID");
        }
        return userMapper.getUserRoles(userId);
    }
}
