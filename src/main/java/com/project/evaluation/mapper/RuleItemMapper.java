package com.project.evaluation.mapper;

import com.project.evaluation.entity.RuleItem;
import com.project.evaluation.vo.RuleItem.AddRuleItemReq;
import com.project.evaluation.vo.RuleItem.UpdateRuleItemReq;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

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
            score_mode,
            dedupe_group,
            coeff,
            module_code,
            submodule_code,
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
            #{scoreMode},
            #{dedupeGroup},
            #{coeff},
            #{moduleCode},
            #{submoduleCode},
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
    @Update("""
            UPDATE `evaluation_rule_item` SET
                rule_id = #{updateRuleItemReq.ruleId},
                item_name = #{updateRuleItemReq.itemName},
                item_type = #{updateRuleItemReq.itemType},
                item_category = #{updateRuleItemReq.itemCategory},
                level = #{updateRuleItemReq.level},
                base_score = #{updateRuleItemReq.baseScore},
                is_competition = #{updateRuleItemReq.isCompetition},
                need_material = #{updateRuleItemReq.needMaterial},
                status = #{updateRuleItemReq.status},
                score_mode = #{updateRuleItemReq.scoreMode},
                dedupe_group = #{updateRuleItemReq.dedupeGroup},
                coeff = #{updateRuleItemReq.coeff},
                module_code = #{updateRuleItemReq.moduleCode},
                submodule_code = #{updateRuleItemReq.submoduleCode},
                update_time = NOW()
            WHERE id = #{id}
            """)
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

    @Select("""
        <script>
        SELECT ri.*
        FROM evaluation_rule_item ri
        INNER JOIN evaluation_rule r ON r.id = ri.rule_id
        WHERE r.period_id = #{periodId}
        <if test="moduleCode != null and moduleCode != ''">
          AND UPPER(IFNULL(ri.module_code,'')) = UPPER(#{moduleCode})
        </if>
        <if test="itemCategory != null">
          AND ri.item_category = #{itemCategory}
        </if>
        ORDER BY
          CASE UPPER(IFNULL(ri.module_code,'')) WHEN 'MORAL' THEN 1 WHEN 'ACADEMIC' THEN 2 WHEN 'QUALITY' THEN 3 ELSE 9 END,
          ri.item_category ASC,
          ri.item_type ASC,
          ri.id ASC
        </script>
        """)
    List<RuleItem> listByPeriod(@Param("periodId") Integer periodId,
                                @Param("moduleCode") String moduleCode,
                                @Param("itemCategory") Integer itemCategory);

    @Delete("DELETE FROM evaluation_rule_item WHERE rule_id = #{ruleId}")
    int deleteByRuleId(@Param("ruleId") Integer ruleId);

    @Select("SELECT * FROM evaluation_rule_item WHERE rule_id = #{ruleId} ORDER BY id ASC")
    List<RuleItem> listByRuleId(@Param("ruleId") Integer ruleId);

    @Select("SELECT COUNT(1) FROM evaluation_rule_item WHERE rule_id = #{ruleId}")
    int countByRuleId(@Param("ruleId") Integer ruleId);

    @Select("""
        SELECT item_category AS itemCategory, COUNT(1) AS cnt
        FROM evaluation_rule_item
        WHERE rule_id = #{ruleId}
        GROUP BY item_category
        ORDER BY item_category
        """)
    List<Map<String, Object>> countByCategory(@Param("ruleId") Integer ruleId);
}
