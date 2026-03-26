package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.vo.User.LoginUserVO;
import com.project.evaluation.vo.Student.AddStudentReq;
import com.project.evaluation.vo.Student.UpdateStudentReq;
import org.springframework.web.multipart.MultipartFile;

public interface StudentService {

    PageBean<LoginUserVO> pageStudents(Integer pageNum, Integer pageSize, String studentId, Integer status);

    void addStudent(AddStudentReq req);

    void updateStudent(Integer id, UpdateStudentReq req);

    void deleteStudent(Integer id);

    int importStudentsByExcel(MultipartFile file);
}
