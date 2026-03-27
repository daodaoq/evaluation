package com.project.evaluation.service;

import com.project.evaluation.entity.Class;
import com.project.evaluation.entity.College;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.vo.User.LoginUserVO;
import com.project.evaluation.vo.Student.AddStudentReq;
import com.project.evaluation.vo.Student.UpdateStudentReq;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentService {

    PageBean<LoginUserVO> pageStudents(Integer pageNum, Integer pageSize, String studentId, Integer status, Integer collegeId, Integer classId);

    void addStudent(AddStudentReq req);

    void updateStudent(Integer id, UpdateStudentReq req);

    void deleteStudent(Integer id);

    int importStudentsByExcel(MultipartFile file);

    /** 学生管理页班级下拉（教师仅见负责班级） */
    List<Class> listClassesForStudentMenu();

    /** 学生管理页学院下拉（教师仅见负责班级所属学院） */
    List<College> listCollegesForStudentMenu();
}
