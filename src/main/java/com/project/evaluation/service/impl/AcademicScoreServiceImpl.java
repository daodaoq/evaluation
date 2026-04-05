package com.project.evaluation.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluation.entity.AcademicScore;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Class;
import com.project.evaluation.entity.College;
import com.project.evaluation.mapper.AcademicScoreMapper;
import com.project.evaluation.service.AcademicScoreService;
import com.project.evaluation.service.ClassService;
import com.project.evaluation.service.CollegeService;
import com.project.evaluation.service.PeriodWorkflowService;
import com.project.evaluation.utils.SecurityContextUtil;
import com.project.evaluation.vo.AcademicScore.AddAcademicScoreReq;
import com.project.evaluation.vo.AcademicScore.ClassOptionVO;
import com.project.evaluation.vo.AcademicScore.MyAcademicScoreVO;
import com.project.evaluation.vo.AcademicScore.UpdateAcademicScoreReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class AcademicScoreServiceImpl implements AcademicScoreService {

    @Autowired
    private AcademicScoreMapper academicScoreMapper;

    @Autowired
    private PeriodWorkflowService periodWorkflowService;

    @Autowired
    private ClassService classService;

    @Autowired
    private CollegeService collegeService;

    @Override
    public PageBean<AcademicScore> pageQuery(Integer pageNum, Integer pageSize, List<Long> periodIds, String studentNo,
                                             List<String> classNames, String studentName) {
        PageHelper.startPage(pageNum, pageSize);
        List<AcademicScore> list = academicScoreMapper.pageQuery(periodIds, studentNo, classNames, studentName);
        PageInfo<AcademicScore> pageInfo = new PageInfo<>(list);
        return new PageBean<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(AddAcademicScoreReq req) {
        periodWorkflowService.assertAcademicMutable(req.getPeriodId());
        AcademicScore score = toEntity(req.getPeriodId(), req.getStudentNo(), req.getClassName(), req.getStudentName(), req.getIntellectualScore());
        if (academicScoreMapper.findByPeriodAndStudentNo(score.getPeriodId(), score.getStudentNo()) != null) {
            throw new IllegalArgumentException("该周期下学号已存在智育记录，请使用编辑或导入更新");
        }
        academicScoreMapper.insert(score);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, UpdateAcademicScoreReq req) {
        if (id == null || id <= 0) throw new IllegalArgumentException("非法ID");
        AcademicScore exists = academicScoreMapper.findById(id);
        if (exists == null) throw new IllegalArgumentException("记录不存在");
        periodWorkflowService.assertAcademicMutable(req.getPeriodId());
        AcademicScore score = toEntity(req.getPeriodId(), req.getStudentNo(), req.getClassName(), req.getStudentName(), req.getIntellectualScore());
        score.setId(id);
        AcademicScore conflict = academicScoreMapper.findByPeriodAndStudentNo(score.getPeriodId(), score.getStudentNo());
        if (conflict != null && !Objects.equals(conflict.getId(), id)) {
            throw new IllegalArgumentException("该周期下学号已存在智育记录");
        }
        academicScoreMapper.updateById(score);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("非法ID");
        AcademicScore exists = academicScoreMapper.findById(id);
        if (exists == null) throw new IllegalArgumentException("记录不存在");
        periodWorkflowService.assertAcademicMutable(exists.getPeriodId());
        if (academicScoreMapper.deleteById(id) == 0) throw new IllegalArgumentException("记录不存在");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importExcel(Long periodId, MultipartFile file) {
        if (periodId == null || periodId <= 0) throw new IllegalArgumentException("请选择综测周期");
        if (academicScoreMapper.countPeriod(periodId) == 0) throw new IllegalArgumentException("综测周期不存在");
        periodWorkflowService.assertAcademicMutable(periodId);
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("请上传 Excel 文件");
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
        if (rows == null || rows.size() < 2) throw new IllegalArgumentException("Excel 至少包含表头和一行数据");

        Map<String, Integer> headerIdx = parseHeader(rows.get(0));
        int idxStudentNo = headerIdx.get("学号");
        int idxClass = headerIdx.get("班级");
        int idxName = headerIdx.get("姓名");
        int idxIntellectual = headerIdx.get("智育");

        List<AcademicScore> valid = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Set<String> duplicateInFile = new HashSet<>();

        for (int i = 1; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            int line = i + 1;
            String studentNo = cell(row, idxStudentNo);
            String className = cell(row, idxClass);
            String studentName = cell(row, idxName);
            String intellectualRaw = cell(row, idxIntellectual);
            if (!StringUtils.hasText(studentNo) || !StringUtils.hasText(className) || !StringUtils.hasText(studentName) || !StringUtils.hasText(intellectualRaw)) {
                errors.add("第" + line + "行：学号、班级、姓名、智育不能为空");
                continue;
            }
            studentNo = studentNo.trim();
            className = className.trim();
            studentName = studentName.trim();
            String rowKey = studentNo;
            if (!duplicateInFile.add(rowKey)) {
                errors.add("第" + line + "行：学号重复(" + studentNo + ")");
                continue;
            }
            BigDecimal intellectual;
            try {
                intellectual = new BigDecimal(intellectualRaw.trim());
            } catch (Exception ex) {
                errors.add("第" + line + "行：智育必须是数值");
                continue;
            }
            if (academicScoreMapper.countStudentNo(studentNo) == 0) {
                errors.add("第" + line + "行：学号不存在(" + studentNo + ")");
                continue;
            }
            MyAcademicScoreVO snapshot = academicScoreMapper.findStudentSnapshotByStudentNo(studentNo);
            if (snapshot == null) {
                errors.add("第" + line + "行：学号不存在(" + studentNo + ")");
                continue;
            }
            if (StringUtils.hasText(snapshot.getStudentName()) && !snapshot.getStudentName().equals(studentName)) {
                errors.add("第" + line + "行：姓名与系统不一致(" + studentNo + ")");
                continue;
            }
            if (StringUtils.hasText(snapshot.getClassName()) && !snapshot.getClassName().equals(className)) {
                errors.add("第" + line + "行：班级与系统不一致(" + studentNo + ")");
                continue;
            }
            AcademicScore score = new AcademicScore();
            score.setPeriodId(periodId);
            score.setStudentNo(studentNo);
            score.setClassName(className);
            score.setStudentName(studentName);
            score.setIntellectualScore(intellectual);
            valid.add(score);
        }

        if (!errors.isEmpty()) {
            int limit = Math.min(errors.size(), 10);
            throw new IllegalArgumentException("导入失败：" + String.join("；", errors.subList(0, limit)));
        }

        int count = 0;
        for (AcademicScore score : valid) {
            AcademicScore exists = academicScoreMapper.findByPeriodAndStudentNo(score.getPeriodId(), score.getStudentNo());
            if (exists == null) {
                academicScoreMapper.insert(score);
            } else {
                score.setId(exists.getId());
                academicScoreMapper.updateById(score);
            }
            count++;
        }
        return count;
    }

    @Override
    public MyAcademicScoreVO getMyScore(Long periodId) {
        if (periodId == null || periodId <= 0) throw new IllegalArgumentException("请选择有效综测周期");
        Integer userId = SecurityContextUtil.getCurrentUserId();
        return academicScoreMapper.findMyScore(userId.longValue(), periodId);
    }

    @Override
    public List<ClassOptionVO> listClassOptionsForAcademic() {
        List<Class> classes = classService.classList();
        if (classes == null || classes.isEmpty()) {
            return List.of();
        }
        List<College> colleges = collegeService.collegeList();
        Map<Integer, String> collegeNames = new HashMap<>();
        if (colleges != null) {
            for (College col : colleges) {
                if (col != null && col.getId() != null) {
                    collegeNames.putIfAbsent(col.getId(), col.getCollegeName() != null ? col.getCollegeName() : "");
                }
            }
        }
        List<ClassOptionVO> out = new ArrayList<>();
        for (Class c : classes) {
            if (c == null) {
                continue;
            }
            ClassOptionVO vo = new ClassOptionVO();
            vo.setId(c.getId());
            vo.setClassName(c.getClassName());
            vo.setCollegeId(c.getCollegeId());
            vo.setCollegeName(collegeNames.getOrDefault(c.getCollegeId(), ""));
            vo.setGradeYear(c.getGradeYear());
            out.add(vo);
        }
        out.sort(Comparator
                .comparing((ClassOptionVO v) -> v.getCollegeName() == null ? "" : v.getCollegeName())
                .thenComparing(v -> v.getClassName() == null ? "" : v.getClassName()));
        return out;
    }

    private AcademicScore toEntity(Long periodId, String studentNo, String className, String studentName, BigDecimal intellectualScore) {
        if (periodId == null || periodId <= 0) throw new IllegalArgumentException("请选择综测周期");
        if (!StringUtils.hasText(studentNo) || !StringUtils.hasText(className) || !StringUtils.hasText(studentName)) {
            throw new IllegalArgumentException("学号、班级、姓名不能为空");
        }
        if (intellectualScore == null) throw new IllegalArgumentException("智育不能为空");
        if (academicScoreMapper.countPeriod(periodId) == 0) throw new IllegalArgumentException("综测周期不存在");
        if (academicScoreMapper.countStudentNo(studentNo.trim()) == 0) throw new IllegalArgumentException("学号不存在");
        MyAcademicScoreVO snapshot = academicScoreMapper.findStudentSnapshotByStudentNo(studentNo.trim());
        if (snapshot != null) {
            if (StringUtils.hasText(snapshot.getStudentName()) && !snapshot.getStudentName().equals(studentName.trim())) {
                throw new IllegalArgumentException("姓名与系统档案不一致");
            }
            if (StringUtils.hasText(snapshot.getClassName()) && !snapshot.getClassName().equals(className.trim())) {
                throw new IllegalArgumentException("班级与系统档案不一致");
            }
        }
        AcademicScore score = new AcademicScore();
        score.setPeriodId(periodId);
        score.setStudentNo(studentNo.trim());
        score.setClassName(className.trim());
        score.setStudentName(studentName.trim());
        score.setIntellectualScore(intellectualScore);
        return score;
    }

    private static String cell(List<Object> row, int idx) {
        if (row == null || idx < 0 || idx >= row.size() || row.get(idx) == null) return "";
        return String.valueOf(row.get(idx)).trim();
    }

    private static Map<String, Integer> parseHeader(List<Object> headerRow) {
        if (headerRow == null || headerRow.isEmpty()) throw new IllegalArgumentException("Excel 表头为空");
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < headerRow.size(); i++) {
            String h = headerRow.get(i) == null ? "" : String.valueOf(headerRow.get(i)).trim();
            if (StringUtils.hasText(h)) idx.put(h, i);
        }
        if (!idx.containsKey("学号") || !idx.containsKey("班级") || !idx.containsKey("姓名") || !idx.containsKey("智育")) {
            throw new IllegalArgumentException("Excel 表头必须包含：学号、班级、姓名、智育");
        }
        return idx;
    }
}
