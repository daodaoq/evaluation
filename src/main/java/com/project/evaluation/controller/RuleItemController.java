package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.entity.RuleItem;
import com.project.evaluation.service.RuleItemService;
import com.project.evaluation.vo.RuleItem.AddRuleItemReq;
import com.project.evaluation.vo.RuleItem.DeleteRuleItemReq;
import com.project.evaluation.vo.RuleItem.UpdateRuleItemReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rule-items")
public class RuleItemController {

    @Autowired
    private RuleItemService ruleItemService;

    /**
     * 添加规则项
     * @param addRuleItemReq
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:item:menu')")
    @CrossOrigin
    public Result addRuleItem(@RequestBody AddRuleItemReq addRuleItemReq){
        RuleItem ruleItem = ruleItemService.findRuleItemByName(addRuleItemReq.getItemName());
        if(ruleItem == null) {
            ruleItemService.addRuleItem(addRuleItemReq);
            return Result.success();
        } else{
            return Result.error("已有周期");
        }
    }

    /**
     * 删除规则项
     * @return
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:item:menu')")
    @CrossOrigin
    public Result deleteRuleItem(@RequestBody DeleteRuleItemReq deleteRuleItemReq) throws IllegalAccessException {
        ruleItemService.deleteRuleItem(deleteRuleItemReq.getId());
        return Result.success();
    }

    /**
     * 更新规则项信息
     * @param updateRuleItemReq
     * @return
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:item:menu')")
    @CrossOrigin
    public Result updateRuleItem(@RequestBody UpdateRuleItemReq updateRuleItemReq, @PathVariable("id") Integer id) {
        RuleItem ruleItem = ruleItemService.findRuleItemById(id);
        if (ruleItem != null) {
            ruleItemService.updateRuleItem(id, updateRuleItemReq);
            return Result.success();
        } else {
            return Result.error("更新的周期不存在");
        }
    }

    /**
     * 批量获取规则项列表
     * @return
     */
    @GetMapping
    @PreAuthorize("hasAuthority('sys:item:menu')")
    @CrossOrigin
    public Result<List<RuleItem>> ruleItemList() {
        List<RuleItem> ruleItems = ruleItemService.ruleItemList();
        return Result.success(ruleItems);
    }

    /**
     * 查询单个规则项详细信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:item:menu')")
    @CrossOrigin
    public Result<RuleItem> findRuleItemById(@PathVariable ("id") Integer id){
        RuleItem ruleItem = ruleItemService.findRuleItemById(id);
        return Result.success(ruleItem);
    }

    /**
     * 分页条件查询规则项
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:item:menu')")
    @CrossOrigin
    public Result<PageBean<RuleItem>> paginationQuery(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize
    ) {
        PageBean<RuleItem> pb = ruleItemService.paginationQuery(pageNum, pageSize);
        return Result.success(pb);
    }
}
