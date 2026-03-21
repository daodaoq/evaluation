package com.project.evaluation.service;

import com.project.evaluation.entity.College;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.vo.College.AddCollegeReq;
import com.project.evaluation.vo.College.UpdateCollegeReq;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface CollegeService {
    College findCollegeByName(String collegeName);
    void addCollege(AddCollegeReq addCollegeReq);
    void deleteCollege(Integer id);
    College findCollegeById(Integer id);
    void updateCollege(Integer id, UpdateCollegeReq updateCollegeReq);
    List<College>collegeList();
    PageBean<College> paginationQuery(Integer pageNum,Integer pageSize,Integer status);
}
