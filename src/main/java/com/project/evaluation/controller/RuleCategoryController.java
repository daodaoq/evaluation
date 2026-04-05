package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.entity.RuleCategory;
import com.project.evaluation.service.RuleCategoryService;
import com.project.evaluation.vo.RuleCategory.AddRuleCategoryReq;
import com.project.evaluation.vo.RuleCategory.CopyRuleCategoriesReq;
import com.project.evaluation.vo.RuleCategory.DeleteRuleCategoryReq;
import com.project.evaluation.vo.RuleCategory.RuleCategoryCopyPreviewVO;
import com.project.evaluation.vo.RuleCategory.UpdateRuleCategoryReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ruleCategory-categories")
public class RuleCategoryController {

    @Autowired
    private RuleCategoryService ruleCategoryService;

    /**
     * 添加规则分类
     * @param addRuleCategoryReq
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:category:menu')")
    @CrossOrigin
    public Result addRuleCategory(@RequestBody AddRuleCategoryReq addRuleCategoryReq){
        RuleCategory ruleCategory = ruleCategoryService.findRuleCategoryByName(addRuleCategoryReq.getCategoryName());
        if(ruleCategory == null) {
            ruleCategoryService.addRuleCategory(addRuleCategoryReq);
            return Result.success();
        } else{
            return Result.error("已有周期");
        }
    }

    /**
     * 删除规则分类
     * @return
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:category:menu')")
    @CrossOrigin
    public Result deleteRuleCategory(@RequestBody DeleteRuleCategoryReq deleteRuleCategoryReq) throws IllegalAccessException {
        ruleCategoryService.deleteRuleCategory(deleteRuleCategoryReq.getId());
        return Result.success();
    }

    /**
     * 更新规则分类信息
     * @param updateRuleCategoryReq
     * @return
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:category:menu')")
    @CrossOrigin
    public Result updateRuleCategory(@RequestBody UpdateRuleCategoryReq updateRuleCategoryReq, @PathVariable("id") Integer id) {
        RuleCategory ruleCategory = ruleCategoryService.findRuleCategoryById(id);
        if (ruleCategory != null) {
            ruleCategoryService.updateRuleCategory(id, updateRuleCategoryReq);
            return Result.success();
        } else {
            return Result.error("更新的周期不存在");
        }
    }

    /**
     * 批量获取规则分类列表
     * @return
     */
    @GetMapping
    @PreAuthorize("hasAuthority('sys:category:menu')")
    @CrossOrigin
    public Result<List<RuleCategory>> ruleCategoryList(
            @RequestParam(required = false) List<Integer> ruleIds) {
        List<RuleCategory> ruleCategorys = ruleCategoryService.ruleCategoryList(ruleIds);
        return Result.success(ruleCategorys);
    }

    /**
     * 查询单个规则分类详细信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:category:menu')")
    @CrossOrigin
    public Result<RuleCategory> findRuleCategoryById(@PathVariable ("id") Integer id){
        RuleCategory ruleCategory = ruleCategoryService.findRuleCategoryById(id);
        return Result.success(ruleCategory);
    }

    /**
     * 分页条件查询规则分类
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:category:menu')")
    @CrossOrigin
    public Result<PageBean<RuleCategory>> paginationQuery(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) List<Integer> ruleIds
    ) {
        PageBean<RuleCategory> pb = ruleCategoryService.paginationQuery(pageNum, pageSize, ruleIds);
        return Result.success(pb);
    }

    @PostMapping("/copy-by-period")
    @PreAuthorize("hasAuthority('sys:category:menu')")
    @CrossOrigin
    public Result<String> copyByPeriod(@RequestBody CopyRuleCategoriesReq req) {
        int copied = ruleCategoryService.copyByPeriod(
                req.getSourcePeriodId(), req.getTargetPeriodId(), req.getOverwrite());
        return Result.success("复制成功，共复制 " + copied + " 条规则分类");
    }

    @GetMapping("/copy-preview")
    @PreAuthorize("hasAuthority('sys:category:menu')")
    @CrossOrigin
    public Result<RuleCategoryCopyPreviewVO> previewCopyByPeriod(
            @RequestParam Integer sourcePeriodId,
            @RequestParam Integer targetPeriodId) {
        return Result.success(ruleCategoryService.previewCopyByPeriod(sourcePeriodId, targetPeriodId));
    }
}
