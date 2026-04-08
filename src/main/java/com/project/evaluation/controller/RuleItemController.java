package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.entity.RuleItem;
import com.project.evaluation.exception.BizException;
import com.project.evaluation.exception.ErrorCode;
import com.project.evaluation.service.RuleItemService;
import com.project.evaluation.vo.RuleItem.AddRuleItemReq;
import com.project.evaluation.vo.RuleItem.CopyRuleItemsReq;
import com.project.evaluation.vo.RuleItem.DeleteRuleItemReq;
import com.project.evaluation.vo.RuleItem.RuleCopyPreviewVO;
import com.project.evaluation.vo.RuleItem.UpdateRuleItemReq;
import jakarta.validation.Valid;
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
    public Result<?> addRuleItem(@Valid @RequestBody AddRuleItemReq addRuleItemReq){
        RuleItem ruleItem = ruleItemService.findRuleItemByName(addRuleItemReq.getItemName());
        if(ruleItem == null) {
            ruleItemService.addRuleItem(addRuleItemReq);
            return Result.success();
        }
        throw new BizException(ErrorCode.BIZ_CONFLICT, "已有周期");
    }

    /**
     * 删除规则项
     * @return
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:item:menu')")
    public Result<?> deleteRuleItem(@Valid @RequestBody DeleteRuleItemReq deleteRuleItemReq) throws IllegalAccessException {
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
    public Result<?> updateRuleItem(@Valid @RequestBody UpdateRuleItemReq updateRuleItemReq, @PathVariable("id") Integer id) {
        RuleItem ruleItem = ruleItemService.findRuleItemById(id);
        if (ruleItem != null) {
            ruleItemService.updateRuleItem(id, updateRuleItemReq);
            return Result.success();
        }
        throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "更新的周期不存在");
    }

    /**
     * 批量获取规则项列表
     * @return
     */
    @GetMapping
    @PreAuthorize("hasAuthority('sys:item:menu')")
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
    public Result<PageBean<RuleItem>> paginationQuery(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize
    ) {
        PageBean<RuleItem> pb = ruleItemService.paginationQuery(pageNum, pageSize);
        return Result.success(pb);
    }

    @GetMapping("/by-period")
    @PreAuthorize("hasAuthority('sys:item:menu')")
    public Result<List<RuleItem>> listByPeriod(
            @RequestParam Integer periodId,
            @RequestParam(required = false) String moduleCode,
            @RequestParam(required = false) Integer itemCategory
    ) {
        return Result.success(ruleItemService.listByPeriod(periodId, moduleCode, itemCategory));
    }

    @PostMapping("/copy-by-period")
    @PreAuthorize("hasAuthority('sys:item:menu')")
    public Result<String> copyByPeriod(@Valid @RequestBody CopyRuleItemsReq req) {
        int copied = ruleItemService.copyByPeriod(req.getSourcePeriodId(), req.getTargetPeriodId(), req.getOverwrite());
        return Result.success("复制成功，共复制 " + copied + " 条规则项");
    }

    @GetMapping("/copy-preview")
    @PreAuthorize("hasAuthority('sys:item:menu')")
    public Result<RuleCopyPreviewVO> previewCopyByPeriod(
            @RequestParam Integer sourcePeriodId,
            @RequestParam Integer targetPeriodId
    ) {
        return Result.success(ruleItemService.previewCopyByPeriod(sourcePeriodId, targetPeriodId));
    }
}
