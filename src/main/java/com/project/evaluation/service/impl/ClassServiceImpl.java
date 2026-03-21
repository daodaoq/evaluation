package com.project.evaluation.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.mapper.ClassMapper;
import com.project.evaluation.entity.Class;
import com.project.evaluation.service.ClassService;
import com.project.evaluation.vo.Class.AddClassReq;
import com.project.evaluation.vo.Class.UpdateClassReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class ClassServiceImpl implements ClassService {
    @Autowired
    private ClassMapper classMapper;

    /**
     * 通过班级姓名查找班级
     *
     * @param name
     * @return
     */
    @Override
    public Class findClassByName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("班级名称不能为空");
        }
        return classMapper.findClassByName(name.trim());
    }

    /**
     * 添加班级
     *
     * @param addClassReq
     */
    @Override
    public void addClass(AddClassReq addClassReq) {
        classMapper.addClass(addClassReq);
        log.info("添加成功：{}", addClassReq);
    }

    /**
     * 删除班级
     *
     * @param id
     */
    @Override
    public void deleteClass(Integer id) {
        if (id <= 0 || id == null) {
            throw new IllegalArgumentException("非法班级ID");
        }
        int rows = classMapper.deleteClass(id);
        if (rows == 0) {
            log.warn("删除失败，班级id不存在：{}", id);
            throw new IllegalStateException("班级不存在或者已删除");
        }
        log.info("删除班级成功：id={}", id);

    }

    /**
     * 通过id查找班级
     *
     * @param id
     * @return
     */
    @Override
    public Class findClassById(Integer id) {
        if (id <= 0 || id == null) {
            throw new IllegalArgumentException("非法班级ID");
        }
        return classMapper.findClassById(id);
    }

    /**
     * 更新班级信息
     *
     * @param id
     * @param updateClassReq
     */
    @Override
    public void updateClass(Integer id, UpdateClassReq updateClassReq) {
        classMapper.updateClass(id, updateClassReq);

    }

    /**
     * 批量获取班级
     *
     * @return
     */
    @Override
    public List<Class> classList() {
        return classMapper.classList();
    }

    /**
     * 分页条件查询班级
     *
     * @param pageNum
     * @param pageSize
     * @param collegeId
     * @param gradeYear
     * @return
     */
    @Override
    public PageBean<Class> paginationQuery(Integer pageNum, Integer pageSize, Integer collegeId, Integer gradeYear) {
        PageBean<Class> pb = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);
        List<Class> classes = classMapper.paginationQuery(collegeId, gradeYear);
        Page<Class> u = (Page<Class>) classes;
        pb.setTotal(u.getTotal());
        pb.setItems(u.getResult());
        return pb;
    }
}
