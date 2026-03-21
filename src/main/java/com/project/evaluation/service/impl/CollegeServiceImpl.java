package com.project.evaluation.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.project.evaluation.entity.College;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.mapper.CollegeMapper;
import com.project.evaluation.service.CollegeService;
import com.project.evaluation.vo.College.AddCollegeReq;
import com.project.evaluation.vo.College.UpdateCollegeReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class CollegeServiceImpl implements CollegeService {
    @Autowired
    private CollegeMapper collegeMapper;

    /**
     * 通过学院名称查找学院
     *
     * @param collegeName
     * @return
     */
    @Override
    public College findCollegeByName(String collegeName) {
        if (!StringUtils.hasText(collegeName)) {
            throw new IllegalArgumentException("学院名称不能为空");
        }
        return collegeMapper.findCollegeByName(collegeName.trim());
    }

    /**
     * 添加学院
     *
     * @param addCollegeReq
     */
    @Override
    public void addCollege(AddCollegeReq addCollegeReq) {
        collegeMapper.addCollege(addCollegeReq);
        log.info("添加成功；{}", addCollegeReq);
    }

    /**
     * 删除学院
     *
     * @param id
     */
    @Override
    public void deleteCollege(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("非法学院ID");
        }
        int rows = collegeMapper.deleteCollege(id);
        if (rows == 0) {
            log.warn("删除失败，学院id不存在：{}", id);
            throw new IllegalStateException("学院不存在或已删除");
        }
        log.info("删除学院成功：id={}", id);

    }

    /**
     * 通过id查找学院
     *
     * @param id
     * @return
     */
    @Override
    public College findCollegeById(Integer id) {
        if (id <= 0 || id == null) {
            throw new IllegalArgumentException("非法学院ID");
        }
        return collegeMapper.findCollegeById(id);
    }

    /**
     * 更新学院信息
     *
     * @param id
     * @param updateCollegeReq
     */
    @Override
    public void updateCollege(Integer id, UpdateCollegeReq updateCollegeReq) {
        collegeMapper.updateCollege(id, updateCollegeReq);
    }

    /**
     * 批量获取学院
     *
     * @return
     */
    @Override
    public List<College> collegeList() {
        return collegeMapper.collegeList();
    }

    /**
     * 分页条件查询学院
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageBean<College> paginationQuery(Integer pageNum, Integer pageSize, Integer status) {
        PageBean<College> pb = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);
        List<College> colleges = collegeMapper.paginationQuery(status);
        Page<College> u = (Page<College>) colleges;
        pb.setTotal(u.getTotal());
        pb.setItems(u.getResult());
        return pb;
    }
}
