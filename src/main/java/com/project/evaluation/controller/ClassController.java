package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.entity.Class;
import com.project.evaluation.exception.BizException;
import com.project.evaluation.exception.ErrorCode;
import com.project.evaluation.service.ClassService;
import com.project.evaluation.vo.Class.AddClassReq;
import com.project.evaluation.vo.Class.DeleteClassReq;
import com.project.evaluation.vo.Class.UpdateClassReq;
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
@RequestMapping("/sys-class")
public class ClassController {

    @Autowired
    private ClassService classService;

    /**
     * 添加班级
     *
     * @param addClassReq
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAuthority('sys:class:menu')")
    public Result<?> addClass(@Valid @RequestBody AddClassReq addClassReq) {
        Class clazz = classService.findClassByName(addClassReq.getClassName());
        if (clazz == null) {
            classService.addClass(addClassReq);
            return Result.success();
        }
        throw new BizException(ErrorCode.BIZ_CONFLICT, "班级已存在");
    }

    /**
     * 删除班级
     *
     * @param deleteClassReq
     * @return
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:class:menu')")
    public Result<?> deleteClass(@Valid @RequestBody DeleteClassReq deleteClassReq) {
        classService.deleteClass(deleteClassReq.getId());
        return Result.success();
    }

    /**
     * 更新班级信息
     *
     * @param updateClassReq
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:class:menu')")
    public Result<?> updateClass(@Valid @RequestBody UpdateClassReq updateClassReq, @PathVariable("id") Integer id) {
        Class clazz = classService.findClassById(id);
        if (clazz != null) {
            classService.updateClass(id, updateClassReq);
            return Result.success();
        }
        throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "班级不存在");
    }

    /**
     * 批量获取班级列表
     *
     * @return
     */
    @GetMapping
    @PreAuthorize("hasAuthority('sys:class:menu')")
    public Result<List<Class>> classList() {
        List<Class> classes = classService.classList();
        return Result.success(classes);
    }

    /**
     * 查询单个班级详细信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:class:menu')")
    public Result<Class> findClassById(@PathVariable("id") Integer id) {
        Class clazz = classService.findClassById(id);
        return Result.success(clazz);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:class:menu')")
    public Result<PageBean<Class>> paginationQuery(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) List<Integer> collegeIds,
            @RequestParam(required = false) List<Integer> gradeYears
    ) {
        PageBean<Class> pb = classService.paginationQuery(pageNum, pageSize, collegeIds, gradeYears);
        return Result.success(pb);
    }
}
