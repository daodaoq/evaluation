package com.project.evaluation.controller;

import com.project.evaluation.entity.Class;
import com.project.evaluation.entity.College;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.StudentService;
import com.project.evaluation.vo.User.LoginUserVO;
import com.project.evaluation.vo.Student.AddStudentReq;
import com.project.evaluation.vo.Student.DeleteStudentReq;
import com.project.evaluation.vo.Student.UpdateStudentReq;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/sys-student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 学生分页（仅含绑定「学生」角色的账号）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<PageBean<LoginUserVO>> list(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) List<Integer> statuses,
            @RequestParam(required = false) List<Integer> collegeIds,
            @RequestParam(required = false) List<Integer> classIds) {
        return Result.success(studentService.pageStudents(pageNum, pageSize, studentId, statuses, collegeIds, classIds));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<?> add(@Valid @RequestBody AddStudentReq req) {
        studentService.addStudent(req);
        return Result.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<?> update(@PathVariable Integer id, @Valid @RequestBody UpdateStudentReq req) {
        studentService.updateStudent(id, req);
        return Result.success();
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<?> delete(@Valid @RequestBody DeleteStudentReq req) {
        studentService.deleteStudent(req.getId());
        return Result.success();
    }

    /**
     * Excel 批量导入学生（必填列：学号、姓名、学院、班级）
     */
    @PostMapping("/import-excel")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<?> importExcel(@RequestParam("file") MultipartFile file) {
        int cnt = studentService.importStudentsByExcel(file);
        return Result.success("导入成功，共 " + cnt + " 条");
    }

    /** 下拉：学院（学生管理页专用，仅需 sys:student:menu） */
    @GetMapping("/colleges")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<List<College>> colleges() {
        return Result.success(studentService.listCollegesForStudentMenu());
    }

    /** 下拉：班级 */
    @GetMapping("/classes")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    public Result<List<Class>> classes() {
        return Result.success(studentService.listClassesForStudentMenu());
    }
}
