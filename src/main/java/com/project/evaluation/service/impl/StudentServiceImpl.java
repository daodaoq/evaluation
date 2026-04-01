package com.project.evaluation.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluation.entity.Class;
import com.project.evaluation.entity.College;
import com.project.evaluation.entity.MyUser;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.mapper.ClassMapper;
import com.project.evaluation.mapper.UserMapper;
import com.project.evaluation.service.ClassService;
import com.project.evaluation.service.CollegeService;
import com.project.evaluation.service.StudentService;
import com.project.evaluation.service.TeacherScopeService;
import com.project.evaluation.vo.Class.AddClassReq;
import com.project.evaluation.vo.College.AddCollegeReq;
import com.project.evaluation.vo.User.LoginUserVO;
import com.project.evaluation.vo.Student.AddStudentReq;
import com.project.evaluation.vo.Student.UpdateStudentReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {

    /** 与种子数据一致：sys_role.id = 1，role_key = STUDENT */
    private static final int STUDENT_ROLE_ID = 1;
    private static final String IMPORT_PASSWORD_PREFIX = "Sdut";
    private static final Pattern TWO_DIGIT_PATTERN = Pattern.compile("(\\d{2})");

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CollegeService collegeService;

    @Autowired
    private ClassService classService;

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private TeacherScopeService teacherScopeService;

    @Override
    public PageBean<LoginUserVO> pageStudents(Integer pageNum, Integer pageSize, String studentId, Integer status, Integer collegeId, Integer classId) {
        TeacherScopeService.StudentMenuScope scope = teacherScopeService.resolveStudentMenuScope();
        if (scope == TeacherScopeService.StudentMenuScope.DENIED) {
            throw new AccessDeniedException("无权限");
        }
        PageBean<LoginUserVO> pb = new PageBean<>();
        if (scope == TeacherScopeService.StudentMenuScope.ADMIN) {
            PageHelper.startPage(pageNum, pageSize);
            List<LoginUserVO> list = userMapper.selectStudentPage(studentId, status, collegeId, classId, STUDENT_ROLE_ID);
            PageInfo<LoginUserVO> info = new PageInfo<>(list);
            pb.setTotal(info.getTotal());
            pb.setItems(info.getList());
            return pb;
        }
        List<Integer> classIds = teacherScopeService.getManagedClassIdsForCurrentTeacher();
        if (classIds == null || classIds.isEmpty()) {
            pb.setTotal(0L);
            pb.setItems(Collections.emptyList());
            return pb;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<LoginUserVO> list = userMapper.selectStudentPageScoped(studentId, status, collegeId, classId, STUDENT_ROLE_ID, classIds);
        PageInfo<LoginUserVO> info = new PageInfo<>(list);
        pb.setTotal(info.getTotal());
        pb.setItems(info.getList());
        return pb;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStudent(AddStudentReq req) {
        if (!StringUtils.hasText(req.getStudentId()) || !StringUtils.hasText(req.getPassword())
                || !StringUtils.hasText(req.getRealName())) {
            throw new IllegalArgumentException("学号、密码、姓名不能为空");
        }
        teacherScopeService.assertCanSetStudentClass(req.getClassId());
        if (userMapper.countByStudentId(req.getStudentId().trim()) > 0) {
            throw new IllegalArgumentException("该学号已存在");
        }
        MyUser u = new MyUser();
        u.setStudentId(req.getStudentId().trim());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRealName(req.getRealName().trim());
        u.setCollegeId(req.getCollegeId());
        u.setClassId(req.getClassId());
        u.setStatus(req.getStatus() != null ? req.getStatus() : 1);
        userMapper.insertUser(u);
        if (u.getId() == null) {
            throw new IllegalStateException("创建用户失败");
        }
        userMapper.addUserRole(u.getId(), STUDENT_ROLE_ID);
        log.info("新增学生账号: id={}, studentId={}", u.getId(), u.getStudentId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStudent(Integer id, UpdateStudentReq req) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("非法用户ID");
        }
        if (userMapper.countUserRole(id, STUDENT_ROLE_ID) == 0) {
            throw new IllegalArgumentException("该用户不是学生或不存在");
        }
        teacherScopeService.assertCanOperateStudentUser(id);
        if (req.getClassId() != null) {
            teacherScopeService.assertCanSetStudentClass(req.getClassId());
        }
        MyUser u = userMapper.selectById(id);
        if (u == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (StringUtils.hasText(req.getStudentId())) {
            String sid = req.getStudentId().trim();
            if (!sid.equals(u.getStudentId()) && userMapper.countByStudentIdExcludeId(sid, id) > 0) {
                throw new IllegalArgumentException("该学号已被占用");
            }
            u.setStudentId(sid);
        }
        if (StringUtils.hasText(req.getPassword())) {
            u.setPassword(passwordEncoder.encode(req.getPassword()));
        } else {
            u.setPassword(null);
        }
        if (req.getRealName() != null) {
            u.setRealName(req.getRealName().trim());
        }
        u.setCollegeId(req.getCollegeId());
        u.setClassId(req.getClassId());
        if (req.getStatus() != null) {
            u.setStatus(req.getStatus());
        }
        userMapper.updateUserSelective(u);
        log.info("更新学生: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudent(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("非法用户ID");
        }
        if (userMapper.countUserRole(id, STUDENT_ROLE_ID) == 0) {
            throw new IllegalArgumentException("该用户不是学生或不存在");
        }
        teacherScopeService.assertCanOperateStudentUser(id);
        userMapper.deleteUserRoles(id);
        userMapper.deleteUserById(id);
        log.info("删除学生账号: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importStudentsByExcel(MultipartFile file) {
        TeacherScopeService.StudentMenuScope scope = teacherScopeService.resolveStudentMenuScope();
        if (scope == TeacherScopeService.StudentMenuScope.DENIED) {
            throw new AccessDeniedException("无权限");
        }
        final List<Integer> teacherManagedClassIds;
        if (scope == TeacherScopeService.StudentMenuScope.TEACHER) {
            teacherManagedClassIds = teacherScopeService.getManagedClassIdsForCurrentTeacher();
            if (teacherManagedClassIds == null || teacherManagedClassIds.isEmpty()) {
                throw new IllegalArgumentException("您尚未被分配负责班级，无法导入");
            }
        } else {
            teacherManagedClassIds = null;
        }
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

        Map<String, Integer> headerIdx = parseHeader(rows.get(0));
        int idxStudentId = headerIdx.get("学号");
        int idxRealName = headerIdx.get("姓名");
        int idxCollege = headerIdx.get("学院");
        int idxClass = headerIdx.get("班级");

        Set<String> sidInFile = new HashSet<>();
        List<ImportStudentRow> validRows = new java.util.ArrayList<>();
        List<String> errors = new java.util.ArrayList<>();

        for (int i = 1; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            String sid = cell(row, idxStudentId);
            String realName = cell(row, idxRealName);
            String collegeName = cell(row, idxCollege);
            String className = cell(row, idxClass);

            int line = i + 1;
            if (!StringUtils.hasText(sid) || !StringUtils.hasText(realName)
                    || !StringUtils.hasText(collegeName) || !StringUtils.hasText(className)) {
                errors.add("第" + line + "行：学号、姓名、学院、班级不能为空");
                continue;
            }
            sid = sid.trim();
            realName = realName.trim();
            collegeName = collegeName.trim();
            className = className.trim();

            if (!sidInFile.add(sid)) {
                errors.add("第" + line + "行：学号重复(" + sid + ")");
                continue;
            }
            if (userMapper.countByStudentId(sid) > 0) {
                errors.add("第" + line + "行：学号已存在(" + sid + ")");
                continue;
            }
            validRows.add(new ImportStudentRow(sid, realName, collegeName, className));
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
        Map<String, Class> classByCollegeAndName = new HashMap<>();
        for (Class cl : classService.classList()) {
            if (cl == null || cl.getCollegeId() == null || !StringUtils.hasText(cl.getClassName())) continue;
            classByCollegeAndName.put(keyOf(cl.getCollegeId(), cl.getClassName().trim()), cl);
        }
        List<ImportStudentRow> importRows = validRows;
        int skippedByScope = 0;
        if (teacherManagedClassIds != null) {
            Set<Integer> managedSet = new HashSet<>(teacherManagedClassIds);
            List<ImportStudentRow> scopedRows = new java.util.ArrayList<>();
            for (ImportStudentRow r : validRows) {
                College c = collegeByName.get(r.collegeName);
                if (c == null || c.getId() == null) {
                    skippedByScope++;
                    continue;
                }
                Class cl = classByCollegeAndName.get(keyOf(c.getId(), r.className));
                if (cl == null || cl.getId() == null || !managedSet.contains(cl.getId())) {
                    skippedByScope++;
                    continue;
                }
                scopedRows.add(r);
            }
            importRows = scopedRows;
        }
        // 先补齐缺失学院（去重创建）
        Set<String> missingColleges = new HashSet<>();
        for (ImportStudentRow r : importRows) {
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

        // 再补齐缺失班级（按 学院+班级 去重创建）
        Set<String> missingClasses = new HashSet<>();
        for (ImportStudentRow r : importRows) {
            College c = collegeByName.get(r.collegeName);
            if (c == null || c.getId() == null) {
                throw new IllegalStateException("学院创建后仍不存在：" + r.collegeName);
            }
            String ck = keyOf(c.getId(), r.className);
            if (!classByCollegeAndName.containsKey(ck)) missingClasses.add(ck);
        }
        int defaultGradeYear = LocalDate.now().getYear();
        for (String ck : missingClasses) {
            String[] arr = ck.split("#", 2);
            Integer collegeId = Integer.valueOf(arr[0]);
            String className = arr[1];

            AddClassReq req = new AddClassReq();
            req.setCollegeId(collegeId);
            req.setClassName(className);
            req.setGradeYear(inferGradeYear(className, defaultGradeYear));
            classService.addClass(req);

            Class created = classMapper.findByCollegeIdAndName(collegeId, className);
            if (created == null || created.getId() == null) {
                throw new IllegalStateException("自动创建班级失败：" + className);
            }
            classByCollegeAndName.put(ck, created);
        }

        int imported = 0;
        for (ImportStudentRow r : importRows) {
            College college = collegeByName.get(r.collegeName);
            if (college == null || college.getId() == null) {
                throw new IllegalStateException("学院不存在：" + r.collegeName);
            }
            Class clazz = classByCollegeAndName.get(keyOf(college.getId(), r.className));
            if (clazz == null || clazz.getId() == null) {
                throw new IllegalStateException("班级不存在：" + r.className);
            }
            if (teacherManagedClassIds != null && !teacherManagedClassIds.contains(clazz.getId())) {
                skippedByScope++;
                continue;
            }
            MyUser u = new MyUser();
            u.setStudentId(r.studentId);
            u.setPassword(passwordEncoder.encode(IMPORT_PASSWORD_PREFIX + r.studentId));
            u.setRealName(r.realName);
            u.setCollegeId(college.getId());
            u.setClassId(clazz.getId());
            u.setStatus(1);
            userMapper.insertUser(u);
            if (u.getId() == null) {
                throw new IllegalStateException("导入失败：创建用户异常(" + r.studentId + ")");
            }
            userMapper.addUserRole(u.getId(), STUDENT_ROLE_ID);
            imported++;
        }
        if (teacherManagedClassIds != null && skippedByScope > 0) {
            log.info("教师导入学生：按负责班级过滤跳过 {} 条", skippedByScope);
        }
        log.info("Excel 导入学生成功：{} 条", imported);
        return imported;
    }

    @Override
    public List<Class> listClassesForStudentMenu() {
        TeacherScopeService.StudentMenuScope scope = teacherScopeService.resolveStudentMenuScope();
        if (scope == TeacherScopeService.StudentMenuScope.DENIED) {
            throw new AccessDeniedException("无权限");
        }
        if (scope == TeacherScopeService.StudentMenuScope.ADMIN) {
            return classService.classList();
        }
        List<Integer> ids = teacherScopeService.getManagedClassIdsForCurrentTeacher();
        return classService.listByIds(ids);
    }

    @Override
    public List<College> listCollegesForStudentMenu() {
        TeacherScopeService.StudentMenuScope scope = teacherScopeService.resolveStudentMenuScope();
        if (scope == TeacherScopeService.StudentMenuScope.DENIED) {
            throw new AccessDeniedException("无权限");
        }
        if (scope == TeacherScopeService.StudentMenuScope.ADMIN) {
            return collegeService.collegeList();
        }
        List<Class> classes = listClassesForStudentMenu();
        Set<Integer> collegeIds = new HashSet<>();
        for (Class cl : classes) {
            if (cl.getCollegeId() != null) {
                collegeIds.add(cl.getCollegeId());
            }
        }
        if (collegeIds.isEmpty()) {
            return Collections.emptyList();
        }
        return collegeService.collegeList().stream()
                .filter(c -> c.getId() != null && collegeIds.contains(c.getId()))
                .collect(Collectors.toList());
    }

    private static String keyOf(Integer collegeId, String className) {
        return collegeId + "#" + className;
    }

    private static String cell(List<Object> row, int idx) {
        if (row == null || idx < 0 || idx >= row.size() || row.get(idx) == null) return "";
        return String.valueOf(row.get(idx)).trim();
    }

    private static Map<String, Integer> parseHeader(List<Object> headerRow) {
        if (headerRow == null || headerRow.isEmpty()) {
            throw new IllegalArgumentException("Excel 表头为空");
        }
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < headerRow.size(); i++) {
            String h = headerRow.get(i) == null ? "" : String.valueOf(headerRow.get(i)).trim();
            if (StringUtils.hasText(h)) idx.put(h, i);
        }
        if (!idx.containsKey("学号") || !idx.containsKey("姓名") || !idx.containsKey("学院") || !idx.containsKey("班级")) {
            throw new IllegalArgumentException("Excel 表头必须包含：学号、姓名、学院、班级");
        }
        return idx;
    }

    /**
     * 从班级名中提取连续两位数字并前缀 20 作为年级。
     * 例：\"计算机23级1班\" -> 2023；提取失败则回退 fallbackYear。
     */
    private static Integer inferGradeYear(String className, int fallbackYear) {
        if (!StringUtils.hasText(className)) return fallbackYear;
        Matcher m = TWO_DIGIT_PATTERN.matcher(className);
        if (!m.find()) return fallbackYear;
        String yy = m.group(1);
        try {
            return Integer.parseInt("20" + yy);
        } catch (NumberFormatException e) {
            return fallbackYear;
        }
    }

    private record ImportStudentRow(String studentId, String realName, String collegeName, String className) {}
}
