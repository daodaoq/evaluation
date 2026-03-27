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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
    @CrossOrigin
    public Result<PageBean<LoginUserVO>> list(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer collegeId,
            @RequestParam(required = false) Integer classId) {
        return Result.success(studentService.pageStudents(pageNum, pageSize, studentId, status, collegeId, classId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<?> add(@RequestBody AddStudentReq req) {
        try {
            studentService.addStudent(req);
            return Result.success();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<?> update(@PathVariable Integer id, @RequestBody UpdateStudentReq req) {
        try {
            studentService.updateStudent(id, req);
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<?> delete(@RequestBody DeleteStudentReq req) {
        try {
            studentService.deleteStudent(req.getId());
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * Excel 批量导入学生（必填列：学号、姓名、学院、班级）
     */
    @PostMapping("/import-excel")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<?> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            int cnt = studentService.importStudentsByExcel(file);
            return Result.success("导入成功，共 " + cnt + " 条");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /** 下拉：学院（学生管理页专用，仅需 sys:student:menu） */
    @GetMapping("/colleges")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<List<College>> colleges() {
        return Result.success(studentService.listCollegesForStudentMenu());
    }

    /** 下拉：班级 */
    @GetMapping("/classes")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<List<Class>> classes() {
        return Result.success(studentService.listClassesForStudentMenu());
    }
}
