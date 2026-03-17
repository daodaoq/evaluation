package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.entity.Rule;
import com.project.evaluation.service.RuleService;
import com.project.evaluation.vo.Rule.AddRuleReq;
import com.project.evaluation.vo.Rule.DeleteRuleReq;
import com.project.evaluation.vo.Rule.UpdateRuleReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluation-rule")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    /**
     * 添加规则总览
     * @param addRuleReq
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:rule:menu')")
    @CrossOrigin
    public Result addRule(@RequestBody AddRuleReq addRuleReq){
        Rule rule = ruleService.findRuleByName(addRuleReq.getRuleName());
        if(rule == null) {
            ruleService.addRule(addRuleReq);
            return Result.success();
        } else{
            return Result.error("已有周期");
        }
    }

    /**
     * 删除规则总览
     * @return
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:rule:menu')")
    @CrossOrigin
    public Result deleteRule(@RequestBody DeleteRuleReq deleteRuleReq) throws IllegalAccessException {
        ruleService.deleteRule(deleteRuleReq.getId());
        return Result.success();
    }

    /**
     * 更新规则总览信息
     * @param updateRuleReq
     * @return
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:rule:menu')")
    @CrossOrigin
    public Result updateRule(@RequestBody UpdateRuleReq updateRuleReq, @PathVariable("id") Integer id) {
        Rule rule = ruleService.findRuleById(id);
        if (rule != null) {
            ruleService.updateRule(id, updateRuleReq);
            return Result.success();
        } else {
            return Result.error("更新的周期不存在");
        }
    }

    /**
     * 批量获取规则总览列表
     * @return
     */
    @GetMapping
    @PreAuthorize("hasAuthority('sys:rule:menu')")
    @CrossOrigin
    public Result<List<Rule>> ruleList() {
        List<Rule> rules = ruleService.ruleList();
        return Result.success(rules);
    }

    /**
     * 查询单个规则总览详细信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:rule:menu')")
    @CrossOrigin
    public Result<Rule> findRuleById(@PathVariable ("id") Integer id){
        Rule rule = ruleService.findRuleById(id);
        return Result.success(rule);
    }

    /**
     * 分页条件查询规则总览
     * @param pageNum
     * @param pageSize
     * @param periodId
     * @param status
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:rule:menu')")
    @CrossOrigin
    public Result<PageBean<Rule>> paginationQuery(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam (required = false) Integer periodId,
            @RequestParam (required = false) Integer status
    ) {
        PageBean<Rule> pb = ruleService.paginationQuery(pageNum, pageSize, periodId, status);
        return Result.success(pb);
    }
}
