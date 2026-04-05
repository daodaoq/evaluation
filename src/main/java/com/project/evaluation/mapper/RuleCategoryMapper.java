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
            "(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)" +
            "VALUES(#{ruleId}, #{categoryName}, #{parentId}, #{scoreCap}, #{studentVisible}, #{sortOrder}, #{categoryBaseScore}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addRuleCategory(AddRuleCategoryReq addRuleCategoryReq);

    @Select("SELECT * FROM `evaluation_rule_item_category` WHERE rule_id = #{ruleId} ORDER BY sort_order ASC, id ASC")
    List<RuleCategory> listByRuleId(@Param("ruleId") Integer ruleId);

    @Delete("DELETE FROM `evaluation_rule_item_category` WHERE rule_id = #{ruleId}")
    int deleteByRuleId(@Param("ruleId") Integer ruleId);

    @Select("SELECT COUNT(1) FROM `evaluation_rule_item_category` WHERE rule_id = #{ruleId}")
    int countByRuleId(@Param("ruleId") Integer ruleId);

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
            "score_cap = #{updateRuleCategoryReq.scoreCap},"+
            "student_visible = #{updateRuleCategoryReq.studentVisible},"+
            "sort_order = #{updateRuleCategoryReq.sortOrder},"+
            "category_base_score = #{updateRuleCategoryReq.categoryBaseScore},"+
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
     * 批量获取规则分类列表；ruleIds 非空时仅返回这些规则总览下的分类
     */
    @Select("""
            <script>
            SELECT * FROM `evaluation_rule_item_category`
            <where>
              <if test="ruleIds != null and ruleIds.size() &gt; 0">
                AND rule_id IN
                <foreach collection="ruleIds" item="rid" open="(" separator="," close=")">
                  #{rid}
                </foreach>
              </if>
            </where>
            ORDER BY sort_order ASC, id ASC
            </script>
            """)
    List<RuleCategory> ruleCategoryList(@Param("ruleIds") List<Integer> ruleIds);

    /**
     * 分页条件查询；ruleIds 非空时按多个规则总览筛选
     */
    @Select("""
            <script>
            SELECT * FROM `evaluation_rule_item_category`
            <where>
              <if test="ruleIds != null and ruleIds.size() &gt; 0">
                AND rule_id IN
                <foreach collection="ruleIds" item="rid" open="(" separator="," close=")">
                  #{rid}
                </foreach>
              </if>
            </where>
            ORDER BY sort_order ASC, id ASC
            </script>
            """)
    List<RuleCategory> paginationQuery(@Param("ruleIds") List<Integer> ruleIds);
}
