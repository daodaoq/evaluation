package com.project.evaluation.service;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.vo.Class.AddClassReq;
import com.project.evaluation.vo.Class.UpdateClassReq;
import com.project.evaluation.entity.Class;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClassService {
    Class findClassByName(String name);

    void addClass(AddClassReq addClassReq);

    void deleteClass(Integer id);

    Class findClassById(Integer id);

    void updateClass(Integer id, UpdateClassReq updateClassReq);

    List<Class> classList();

    List<Class> listByIds(List<Integer> ids);

    PageBean<Class> paginationQuery(Integer pageNum, Integer pageSize, Integer collegeId, Integer gradeYear);

}
