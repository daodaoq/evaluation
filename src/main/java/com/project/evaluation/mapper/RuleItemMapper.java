package com.project.evaluation.mapper;

import com.project.evaluation.entity.RuleItem;
import com.project.evaluation.vo.RuleItem.AddRuleItemReq;
import com.project.evaluation.vo.RuleItem.UpdateRuleItemReq;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RuleItemMapper {
    
    /**
     * 添加规则项
     * @param addRuleItemReq
     */
    /**
     * 添加规则项
     */
    @Insert("""
        INSERT INTO evaluation_rule_item (
        rule_id,
        item_name,
        item_type,
        item_category,
        level,
        base_score,
        is_competition,
        need_material,
        status,
        create_time,
        update_time
        ) VALUES (
            #{ruleId},
            #{itemName},
            #{itemType},
            #{itemCategory},
            #{level},
            #{baseScore},
            #{isCompetition},
            #{needMaterial},
            #{status},
            NOW(),
            NOW()
        )
    """)
    void addRuleItem(AddRuleItemReq addRuleItemReq);


    /**
     * 删除规则项
     * @param id
     */
    @Delete("DELETE FROM `evaluation_rule_item` WHERE id = #{id}")
    int deleteRuleItem(Integer id);

    /**
     * 更新规则项信息
     * @param updateRuleItemReq
     */
    @Update("UPDATE `evaluation_rule_item` SET "+
            "rule_id = #{updateRuleItemReq.ruleId},"+
            "item_name = #{updateRuleItemReq.itemName},"+
            "item_type = #{updateRuleItemReq.itemType},"+
            "item_category = #{updateRuleItemReq.itemCategory}," +
            "level = #{updateRuleItemReq.level}," +
            "base_score = #{updateRuleItemReq.baseScore}," +
            "is_competition = #{updateRuleItemReq.isCompetition}," +
            "need_material = #{updateRuleItemReq.needMaterial}," +
            "status = #{updateRuleItemReq.status}," +
            "update_time = NOW() WHERE id = #{id}")
    void updateRuleItem(Integer id, UpdateRuleItemReq updateRuleItemReq);

    /**
     * 根据规则项名称查找规则项
     * @param name
     * @rerurn
     */
    @Select("SELECT * FROM `evaluation_rule_item` WHERE item_name = #{name}")
    RuleItem findRuleItemByName(String name);

    /**
     * 通过 id 查找规则项
     * @param id
     * @return
     */
    @Select("SELECT * FROM `evaluation_rule_item` WHERE id = #{id}")
    RuleItem findRuleItemById(Integer id);

    /**
     * 批量获取规则项列表
     * @return
     */
    @Select("SELECT * FROM `evaluation_rule_item`")
    List<RuleItem> ruleItemList();

    /**
     * 分页条件查询规则项
     * @return
     */
    @Select("SELECT * FROM `evaluation_rule_item`")
    List<RuleItem> paginationQuery();
}
