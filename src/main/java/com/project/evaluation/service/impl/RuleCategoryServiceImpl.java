package com.project.evaluation.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.RuleCategory;
import com.project.evaluation.mapper.RuleCategoryMapper;
import com.project.evaluation.service.RuleCategoryService;
import com.project.evaluation.vo.RuleCategory.AddRuleCategoryReq;
import com.project.evaluation.vo.RuleCategory.UpdateRuleCategoryReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class RuleCategoryServiceImpl implements RuleCategoryService {

    @Autowired
    private RuleCategoryMapper ruleCategoryMapper;

    /**
     * 添加规则分类
     * @param addRuleCategoryReq
     */
    @Override
    public void addRuleCategory(AddRuleCategoryReq addRuleCategoryReq) {
        ruleCategoryMapper.addRuleCategory(addRuleCategoryReq);
        log.info("添加成功：{}",addRuleCategoryReq);
    }

    /**
     * 删除规则分类
     * @param id
     */
    @Override
    public void deleteRuleCategory(Integer id)  {
        if(id == null || id <= 0){
            throw new IllegalArgumentException("非法规则分类ID");
        }
        int rows = ruleCategoryMapper.deleteRuleCategory(id);
        if(rows == 0)
        {
            log.warn("删除失败，规则分类id不存在：{}",id);
            throw new IllegalStateException("规则分类不存在或已删除");
        }
        log.info("删除规则分类成功： id={}",id);

    }

    /**
     * 更新规则分类信息
     * @param updateRuleCategoryReq
     */
    @Override
    public void updateRuleCategory(Integer id, UpdateRuleCategoryReq updateRuleCategoryReq) {
        ruleCategoryMapper.updateRuleCategory(id, updateRuleCategoryReq);
    }

    /**
     * 通过周其名称查找规则分类
     * @param name
     * @return RuleCategory
     */
    @Override
    public RuleCategory findRuleCategoryByName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("规则分类名称不能为空");
        }
        return ruleCategoryMapper.findRuleCategoryByName(name.trim());
    }

    /**
     * 通过 id 查找规则分类
     * @param id
     * @return
     */
    @Override
    public RuleCategory findRuleCategoryById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("非法规则分类ID");
        }
        return ruleCategoryMapper.findRuleCategoryById(id);
    }

    /**
     * 批量获取规则分类
     * @return
     */
    @Override
    public List<RuleCategory> ruleCategoryList() {
        return ruleCategoryMapper.ruleCategoryList();
    }

    /**
     * 分页条件查询规则分类
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageBean<RuleCategory> paginationQuery(Integer pageNum, Integer pageSize) {
        PageBean<RuleCategory> pb = new PageBean<>();

        PageHelper.startPage(pageNum, pageSize);

        List<RuleCategory> ruleCategorys = ruleCategoryMapper.paginationQuery();

        Page<RuleCategory> u = (Page<RuleCategory>) ruleCategorys;

        pb.setTotal(u.getTotal());
        pb.setItems(u.getResult());
        return pb;
    }
}
