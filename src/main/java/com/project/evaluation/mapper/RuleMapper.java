package com.project.evaluation.mapper;

import com.project.evaluation.entity.Rule;
import com.project.evaluation.vo.Rule.AddRuleReq;
import com.project.evaluation.vo.Rule.UpdateRuleReq;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RuleMapper {
    
    /**
     * 添加规则总览
     * @param addRuleReq
     */
    @Insert("INSERT INTO `evaluation-rule`" +
            "(period_id, rule_name, version_code, status, create_time, update_time)"+
            "VALUES(#{periodId}, #{ruleName}, #{versionCode}, #{status}, NOW(), NOW())")
    void addRule(AddRuleReq addRuleReq);

    /**
     * 删除规则总览
     * @param id
     */
    @Delete("DELETE FROM `evaluation-rule` WHERE id = #{id}")
    int deleteRule(Integer id);

    /**
     * 更新规则总览信息
     * @param updateRuleReq
     */
    @Update("UPDATE `evaluation-rule` SET "+
            "period_id = #{updateRuleReq.periodId},"+
            "rule_name = #{updateRuleReq.ruleName},"+
            "version_code = #{updateRuleReq.versionCode},"+
            "status = #{updateRuleReq.status}," +
            "update_time = NOW() WHERE id = #{id}")
    void updateRule(Integer id, UpdateRuleReq updateRuleReq);

    /**
     * 根据规则总览名称查找规则总览
     * @param name
     * @rerurn
     */
    @Select("SELECT * FROM `evaluation-rule` WHERE rule_name = #{name}")
    Rule findRuleByName(String name);

    /**
     * 通过 id 查找规则总览
     * @param id
     * @return
     */
    @Select("SELECT * FROM `evaluation-rule` WHERE id = #{id}")
    Rule findRuleById(Integer id);

    /**
     * 批量获取规则总览列表
     * @return
     */
    @Select("SELECT * FROM `evaluation-rule`")
    List<Rule> ruleList();

    /**
     * 分页条件查询规则总览
     * @param periodId
     * @param status
     * @return
     */
    List<Rule> paginationQuery(Integer periodId, Integer status);
}
