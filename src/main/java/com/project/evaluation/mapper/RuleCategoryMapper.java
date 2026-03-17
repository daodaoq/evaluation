package com.project.evaluation.mapper;

import com.project.evaluation.entity.RuleCategory;
import com.project.evaluation.vo.RuleCategory.AddRuleCategoryReq;
import com.project.evaluation.vo.RuleCategory.UpdateRuleCategoryReq;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RuleCategoryMapper {
    
    /**
     * 添加规则分类
     * @param addRuleCategoryReq
     */
    @Insert("INSERT INTO `evaluation_rule_item_category`" +
            "(rule_id, category_name, parent_id, create_time, update_time)"+
            "VALUES(#{ruleId}, #{categoryName}, #{parentId}, NOW(), NOW())")
    void addRuleCategory(AddRuleCategoryReq addRuleCategoryReq);

    /**
     * 删除规则分类
     * @param id
     */
    @Delete("DELETE FROM `evaluation_rule_item_category` WHERE id = #{id}")
    int deleteRuleCategory(Integer id);

    /**
     * 更新规则分类信息
     * @param updateRuleCategoryReq
     */
    @Update("UPDATE `evaluation_rule_item_category` SET "+
            "rule_id = #{updateRuleCategoryReq.ruleId},"+
            "category_name = #{updateRuleCategoryReq.categoryName},"+
            "parent_id = #{updateRuleCategoryReq.parentId},"+
            "update_time = NOW() WHERE id = #{id}")
    void updateRuleCategory(Integer id, UpdateRuleCategoryReq updateRuleCategoryReq);

    /**
     * 根据规则分类名称查找规则分类
     * @param name
     * @rerurn
     */
    @Select("SELECT * FROM `evaluation_rule_item_category` WHERE category_name = #{name}")
    RuleCategory findRuleCategoryByName(String name);

    /**
     * 通过 id 查找规则分类
     * @param id
     * @return
     */
    @Select("SELECT * FROM `evaluation_rule_item_category` WHERE id = #{id}")
    RuleCategory findRuleCategoryById(Integer id);

    /**
     * 批量获取规则分类列表
     * @return
     */
    @Select("SELECT * FROM `evaluation_rule_item_category`")
    List<RuleCategory> ruleCategoryList();

    /**
     * 分页条件查询规则分类
     * @return
     */
    @Select("SELECT * FROM `evaluation_rule_item_category`")
    List<RuleCategory> paginationQuery();
}
