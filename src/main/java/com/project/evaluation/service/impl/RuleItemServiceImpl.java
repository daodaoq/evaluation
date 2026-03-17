package com.project.evaluation.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.RuleItem;
import com.project.evaluation.mapper.RuleItemMapper;
import com.project.evaluation.service.RuleItemService;
import com.project.evaluation.vo.RuleItem.AddRuleItemReq;
import com.project.evaluation.vo.RuleItem.UpdateRuleItemReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class RuleItemServiceImpl implements RuleItemService {

    @Autowired
    private RuleItemMapper ruleItemMapper;

    /**
     * 添加规则项
     * @param addRuleItemReq
     */
    @Override
    public void addRuleItem(AddRuleItemReq addRuleItemReq) {
        ruleItemMapper.addRuleItem(addRuleItemReq);
        log.info("添加成功：{}",addRuleItemReq);
    }

    /**
     * 删除规则项
     * @param id
     */
    @Override
    public void deleteRuleItem(Integer id)  {
        if(id == null || id <= 0){
            throw new IllegalArgumentException("非法规则项ID");
        }
        int rows = ruleItemMapper.deleteRuleItem(id);
        if(rows == 0)
        {
            log.warn("删除失败，规则项id不存在：{}",id);
            throw new IllegalStateException("规则项不存在或已删除");
        }
        log.info("删除规则项成功： id={}",id);

    }

    /**
     * 更新规则项信息
     * @param updateRuleItemReq
     */
    @Override
    public void updateRuleItem(Integer id, UpdateRuleItemReq updateRuleItemReq) {
        ruleItemMapper.updateRuleItem(id, updateRuleItemReq);
    }

    /**
     * 通过周其名称查找规则项
     * @param name
     * @return RuleItem
     */
    @Override
    public RuleItem findRuleItemByName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("规则项名称不能为空");
        }
        return ruleItemMapper.findRuleItemByName(name.trim());
    }

    /**
     * 通过 id 查找规则项
     * @param id
     * @return
     */
    @Override
    public RuleItem findRuleItemById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("非法规则项ID");
        }
        return ruleItemMapper.findRuleItemById(id);
    }

    /**
     * 批量获取规则项
     * @return
     */
    @Override
    public List<RuleItem> ruleItemList() {
        return ruleItemMapper.ruleItemList();
    }

    /**
     * 分页条件查询规则项
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageBean<RuleItem> paginationQuery(Integer pageNum, Integer pageSize) {
        PageBean<RuleItem> pb = new PageBean<>();

        PageHelper.startPage(pageNum, pageSize);

        List<RuleItem> ruleItems = ruleItemMapper.paginationQuery();

        Page<RuleItem> u = (Page<RuleItem>) ruleItems;

        pb.setTotal(u.getTotal());
        pb.setItems(u.getResult());
        return pb;
    }
}
