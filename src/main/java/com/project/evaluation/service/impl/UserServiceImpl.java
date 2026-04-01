package com.project.evaluation.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluation.entity.Authority;
import com.project.evaluation.entity.College;
import com.project.evaluation.entity.MyUser;
import com.project.evaluation.entity.MyUserDetails;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.entity.SysPermission;
import com.project.evaluation.mapper.AuthorityMapper;
import com.project.evaluation.mapper.SysPermissionMapper;
import com.project.evaluation.mapper.TeacherClassMapper;
import com.project.evaluation.mapper.UserMapper;
import com.project.evaluation.service.ClassService;
import com.project.evaluation.service.CollegeService;
import com.project.evaluation.service.UserService;
import com.project.evaluation.utils.JwtUtil;
import com.project.evaluation.utils.SecurityContextUtil;
import com.project.evaluation.vo.College.AddCollegeReq;
import com.project.evaluation.vo.User.AddTeacherReq;
import com.project.evaluation.vo.User.LoginReq;
import com.project.evaluation.vo.User.LoginResp;
import com.project.evaluation.vo.User.LoginUserVO;
import com.project.evaluation.vo.User.UpdateTeacherReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    /** 与 JWT、Redis 会话一致：24 小时 */
    private static final long LOGIN_TOKEN_TTL_MS = 1000L * 60 * 60 * 24;
    private static final int STUDENT_ROLE_ID = 1;
    private static final int TEACHER_ROLE_ID = 2;
    private static final int ADMIN_ROLE_ID = 3;
    private static final String IMPORT_PASSWORD_PREFIX = "Sdut";

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

    @Autowired
    private CollegeService collegeService;

    @Autowired
    private ClassService classService;

    @Autowired
    private TeacherClassMapper teacherClassMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
            Integer uid = myUser.getId();
            boolean hasStudentRole = userMapper.countUserRole(uid, STUDENT_ROLE_ID) > 0;
            boolean hasTeacherRole = userMapper.countUserRole(uid, TEACHER_ROLE_ID) > 0;
            boolean hasAdminRole = userMapper.countUserRole(uid, ADMIN_ROLE_ID) > 0;
            if (!hasStudentRole && !hasTeacherRole && !hasAdminRole) {
                return Result.error("账号未分配角色，无法登录");
            }
            String jwtKey = "user:" + myUser.getId();
            String token = JwtUtil.createToken(jwtKey, LOGIN_TOKEN_TTL_MS);
            long expireAt = System.currentTimeMillis() + LOGIN_TOKEN_TTL_MS;

            redisTemplate.opsForValue().set(jwtKey, principal, LOGIN_TOKEN_TTL_MS, TimeUnit.MILLISECONDS);

            LoginUserVO userVo = toLoginUserVO(myUser);
            return Result.success(new LoginResp(token, expireAt, userVo));
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
    public PageBean<LoginUserVO> paginationQueryUsers(Integer pageNum, Integer pageSize, String studentId, Integer status, Integer collegeId) {
        PageBean<LoginUserVO> pb = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);
        List<LoginUserVO> list = userMapper.selectUserPage(studentId, status, collegeId);
        PageInfo<LoginUserVO> info = new PageInfo<>(list);
        pb.setTotal(info.getTotal());
        pb.setItems(info.getList());
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importTeachersByExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请上传 Excel 文件");
        }
        String fn = file.getOriginalFilename();
        if (!StringUtils.hasText(fn) || (!fn.endsWith(".xlsx") && !fn.endsWith(".xls"))) {
            throw new IllegalArgumentException("仅支持 .xlsx / .xls 文件");
        }

        List<List<Object>> rows;
        try {
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            rows = reader.read();
        } catch (IOException e) {
            throw new IllegalArgumentException("读取 Excel 失败");
        }
        if (rows == null || rows.size() < 2) {
            throw new IllegalArgumentException("Excel 至少包含表头和一行数据");
        }

        Map<String, Integer> header = parseTeacherHeader(rows.get(0));
        int idxNo = header.get("工号");
        int idxName = header.get("姓名");
        int idxCollege = header.get("学院");

        Set<String> noInFile = new HashSet<>();
        List<ImportTeacherRow> valid = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            String no = cell(row, idxNo);
            String realName = cell(row, idxName);
            String collegeName = cell(row, idxCollege);
            int line = i + 1;
            if (!StringUtils.hasText(no) || !StringUtils.hasText(realName) || !StringUtils.hasText(collegeName)) {
                errors.add("第" + line + "行：工号、姓名、学院不能为空");
                continue;
            }
            no = no.trim();
            realName = realName.trim();
            collegeName = collegeName.trim();

            if (!noInFile.add(no)) {
                errors.add("第" + line + "行：工号重复(" + no + ")");
                continue;
            }
            if (userMapper.countByStudentId(no) > 0) {
                errors.add("第" + line + "行：工号已存在(" + no + ")");
                continue;
            }
            valid.add(new ImportTeacherRow(no, realName, collegeName));
        }
        if (!errors.isEmpty()) {
            int limit = Math.min(errors.size(), 10);
            throw new IllegalArgumentException("导入失败：" + String.join("；", errors.subList(0, limit)));
        }

        Map<String, College> collegeByName = new HashMap<>();
        for (College c : collegeService.collegeList()) {
            if (c != null && StringUtils.hasText(c.getCollegeName())) {
                collegeByName.put(c.getCollegeName().trim(), c);
            }
        }
        Set<String> missingColleges = new HashSet<>();
        for (ImportTeacherRow r : valid) {
            if (!collegeByName.containsKey(r.collegeName)) missingColleges.add(r.collegeName);
        }
        for (String cname : missingColleges) {
            AddCollegeReq req = new AddCollegeReq();
            req.setCollegeName(cname);
            req.setStatus(1);
            collegeService.addCollege(req);
            College created = collegeService.findCollegeByName(cname);
            if (created == null || created.getId() == null) {
                throw new IllegalStateException("自动创建学院失败：" + cname);
            }
            collegeByName.put(cname, created);
        }

        for (ImportTeacherRow r : valid) {
            College c = collegeByName.get(r.collegeName);
            if (c == null || c.getId() == null) {
                throw new IllegalStateException("学院不存在：" + r.collegeName);
            }
            MyUser u = new MyUser();
            u.setStudentId(r.jobNo);
            u.setPassword(passwordEncoder.encode(IMPORT_PASSWORD_PREFIX + r.jobNo));
            u.setRealName(r.realName);
            u.setCollegeId(c.getId());
            u.setClassId(null);
            u.setStatus(1);
            userMapper.insertUser(u);
            if (u.getId() == null) {
                throw new IllegalStateException("创建教师账号失败(" + r.jobNo + ")");
            }
            userMapper.addUserRole(u.getId(), TEACHER_ROLE_ID);
        }
        return valid.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAssignSameRole(List<Integer> userIds, Integer roleId) {
        if (CollectionUtils.isEmpty(userIds)) {
            throw new IllegalArgumentException("请选择要赋角色的用户");
        }
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("角色不能为空");
        }
        int affected = 0;
        for (Integer userId : userIds) {
            if (userId == null || userId <= 0) continue;
            if (userMapper.countUserRole(userId, roleId) > 0) continue;
            userMapper.addUserRole(userId, roleId);
            affected++;
        }
        return affected;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTeacher(AddTeacherReq req) {
        if (req == null || !StringUtils.hasText(req.getTeacherNo()) || !StringUtils.hasText(req.getPassword())
                || !StringUtils.hasText(req.getRealName())) {
            throw new IllegalArgumentException("工号、密码、姓名不能为空");
        }
        String teacherNo = req.getTeacherNo().trim();
        if (userMapper.countByStudentId(teacherNo) > 0) {
            throw new IllegalArgumentException("该工号已存在");
        }
        MyUser u = new MyUser();
        u.setStudentId(teacherNo);
        u.setPassword(passwordEncoder.encode(req.getPassword().trim()));
        u.setRealName(req.getRealName().trim());
        u.setCollegeId(req.getCollegeId());
        u.setClassId(null);
        u.setStatus(req.getStatus() != null ? req.getStatus() : 1);
        userMapper.insertUser(u);
        if (u.getId() == null) {
            throw new IllegalStateException("创建教师账号失败");
        }
        userMapper.addUserRole(u.getId(), TEACHER_ROLE_ID);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTeacher(Integer id, UpdateTeacherReq req) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("非法用户ID");
        }
        if (!isTeacherOrAdmin(id)) {
            throw new IllegalArgumentException("该用户不是教师/管理员或不存在");
        }
        MyUser u = userMapper.selectById(id);
        if (u == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (StringUtils.hasText(req.getTeacherNo())) {
            String teacherNo = req.getTeacherNo().trim();
            if (!teacherNo.equals(u.getStudentId()) && userMapper.countByStudentIdExcludeId(teacherNo, id) > 0) {
                throw new IllegalArgumentException("该工号已被占用");
            }
            u.setStudentId(teacherNo);
        }
        if (StringUtils.hasText(req.getPassword())) {
            u.setPassword(passwordEncoder.encode(req.getPassword().trim()));
        } else {
            u.setPassword(null);
        }
        if (req.getRealName() != null) {
            u.setRealName(req.getRealName().trim());
        }
        u.setCollegeId(req.getCollegeId());
        u.setClassId(null);
        if (req.getStatus() != null) {
            u.setStatus(req.getStatus());
        }
        userMapper.updateUserSelective(u);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeacher(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("非法用户ID");
        }
        if (!isTeacherOrAdmin(id)) {
            throw new IllegalArgumentException("该用户不是教师/管理员或不存在");
        }
        teacherClassMapper.deleteByTeacherUserId(id);
        userMapper.deleteUserRoles(id);
        userMapper.deleteUserById(id);
    }

    @Override
    public List<College> listColleges() {
        return collegeService.collegeList();
    }

    @Override
    public List<Integer> getTeacherClassIds(Integer teacherUserId) {
        if (!isCurrentUserAdmin()) {
            throw new AccessDeniedException("仅管理员可操作");
        }
        if (teacherUserId == null || teacherUserId <= 0) {
            throw new IllegalArgumentException("非法教师用户ID");
        }
        if (userMapper.countUserRole(teacherUserId, TEACHER_ROLE_ID) == 0) {
            throw new IllegalArgumentException("该用户不是教师");
        }
        return teacherClassMapper.selectClassIdsByTeacherUserId(teacherUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setTeacherClasses(Integer teacherUserId, List<Integer> classIds) {
        if (!isCurrentUserAdmin()) {
            throw new AccessDeniedException("仅管理员可操作");
        }
        if (teacherUserId == null || teacherUserId <= 0) {
            throw new IllegalArgumentException("非法教师用户ID");
        }
        if (userMapper.countUserRole(teacherUserId, TEACHER_ROLE_ID) == 0) {
            throw new IllegalArgumentException("该用户不是教师");
        }
        teacherClassMapper.deleteByTeacherUserId(teacherUserId);
        if (classIds == null || classIds.isEmpty()) {
            return;
        }
        Set<Integer> seen = new HashSet<>();
        for (Integer cid : classIds) {
            if (cid == null || !seen.add(cid)) {
                continue;
            }
            if (classService.findClassById(cid) == null) {
                throw new IllegalArgumentException("班级不存在: " + cid);
            }
            teacherClassMapper.insert(teacherUserId, cid);
        }
    }

    private boolean isCurrentUserAdmin() {
        Integer uid = SecurityContextUtil.getCurrentUserId();
        List<Integer> roles = userMapper.getUserRoles(uid);
        return roles != null && roles.contains(ADMIN_ROLE_ID);
    }

    private static String cell(List<Object> row, int idx) {
        if (row == null || idx < 0 || idx >= row.size() || row.get(idx) == null) return "";
        return String.valueOf(row.get(idx)).trim();
    }

    private static Map<String, Integer> parseTeacherHeader(List<Object> headerRow) {
        if (headerRow == null || headerRow.isEmpty()) {
            throw new IllegalArgumentException("Excel 表头为空");
        }
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < headerRow.size(); i++) {
            String h = headerRow.get(i) == null ? "" : String.valueOf(headerRow.get(i)).trim();
            if (StringUtils.hasText(h)) idx.put(h, i);
        }
        // 兼容旧模板“真实姓名”，统一映射为“姓名”
        if (!idx.containsKey("姓名") && idx.containsKey("真实姓名")) {
            idx.put("姓名", idx.get("真实姓名"));
        }
        if (!idx.containsKey("工号") || !idx.containsKey("姓名") || !idx.containsKey("学院")) {
            throw new IllegalArgumentException("Excel 表头必须包含：工号、姓名、学院");
        }
        return idx;
    }

    private boolean isTeacherOrAdmin(Integer userId) {
        return userMapper.countUserRole(userId, TEACHER_ROLE_ID) > 0
                || userMapper.countUserRole(userId, ADMIN_ROLE_ID) > 0;
    }

    private record ImportTeacherRow(String jobNo, String realName, String collegeName) {}
}
