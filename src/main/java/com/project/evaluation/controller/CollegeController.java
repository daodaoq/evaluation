package com.project.evaluation.controller;

import com.project.evaluation.entity.College;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.exception.BizException;
import com.project.evaluation.exception.ErrorCode;
import com.project.evaluation.service.CollegeService;
import com.project.evaluation.vo.College.AddCollegeReq;
import com.project.evaluation.vo.College.DeleteCollegeReq;
import com.project.evaluation.vo.College.UpdateCollegeReq;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sys-college")
public class CollegeController {

    @Autowired
    private CollegeService collegeService;

    /**
     * 添加学院
     *
     * @param addCollegeReq
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:college:menu')")
    public Result<?> addCollege(@Valid @RequestBody AddCollegeReq addCollegeReq) {
        College college = collegeService.findCollegeByName(addCollegeReq.getCollegeName());
        if (college == null) {
            collegeService.addCollege(addCollegeReq);
            return Result.success();
        }
        throw new BizException(ErrorCode.BIZ_CONFLICT, "学院已存在");
    }

    /**
     * 删除学院
     *
     * @param deleteCollegeReq
     * @return
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:college:menu')")
    public Result<?> deleteCollege(@Valid @RequestBody DeleteCollegeReq deleteCollegeReq) {
        collegeService.deleteCollege(deleteCollegeReq.getId());
        return Result.success();
    }

    /**
     * 更新学院信息
     *
     * @param updateCollegeReq
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:college:menu')")
    public Result<?> updateCollege(@Valid @RequestBody UpdateCollegeReq updateCollegeReq, @PathVariable("id") Integer id) {
        College college = collegeService.findCollegeById(id);
        if (college != null) {
            collegeService.updateCollege(id, updateCollegeReq);
            return Result.success();
        }
        throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "学院不存在");
    }

    /**
     * 批量获取学院列表
     *
     * @return
     */
    @GetMapping
    @PreAuthorize("hasAuthority('sys:college:menu')")
    public Result<List<College>> collegeList() {
        List<College> colleges = collegeService.collegeList();
        return Result.success(colleges);
    }

    /**
     * 查询单个学院详细信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:college:menu')")
    public Result<College> findCollegeById(@PathVariable("id") Integer id) {
        College college = collegeService.findCollegeById(id);
        return Result.success(college);
    }

    /**
     * 分页条件查询学院
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:college:menu')")
    public Result<PageBean<College>> paginationQuery(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) List<Integer> statuses
    ) {
        PageBean<College> pb = collegeService.paginationQuery(pageNum, pageSize, statuses);
        return Result.success(pb);
    }
}
