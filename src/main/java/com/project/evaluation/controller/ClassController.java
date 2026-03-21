package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.entity.Class;
import com.project.evaluation.service.ClassService;
import com.project.evaluation.vo.Class.AddClassReq;
import com.project.evaluation.vo.Class.DeleteClassReq;
import com.project.evaluation.vo.Class.UpdateClassReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @CrossOrigin
    public Result addClass(@RequestBody AddClassReq addClassReq) {
        Class clazz = classService.findClassByName(addClassReq.getClassName());
        if (clazz == null) {
            classService.addClass(addClassReq);
            return Result.success();
        } else {
            return Result.error("班级已存在");
        }
    }

    /**
     * 删除班级
     *
     * @param deleteClassReq
     * @return
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:class:menu')")
    @CrossOrigin
    public Result deleteClass(@RequestBody DeleteClassReq deleteClassReq) {
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
    @PutMapping
    @PreAuthorize("hasAuthority('sys:class:menu')")
    @CrossOrigin
    public Result updateClass(@RequestBody UpdateClassReq updateClassReq, @PathVariable("id") Integer id) {
        Class clazz = classService.findClassById(id);
        if (clazz != null) {
            classService.updateClass(id, updateClassReq);
            return Result.success();
        } else {
            return Result.error("班级不存在");
        }
    }

    /**
     * 批量获取班级列表
     *
     * @return
     */
    @GetMapping
    @PreAuthorize("hasAuthority('sys:class:menu')")
    @CrossOrigin
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
    @GetMapping
    @PreAuthorize("hasAuthority('sys:class:menu')")
    @CrossOrigin
    public Result<Class> findClassById(@PathVariable("id") Integer id) {
        Class clazz = classService.findClassById(id);
        return Result.success(clazz);
    }

    public Result<PageBean<Class>> paginationQuery(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) Integer collegedId,
            @RequestParam(required = false) Integer gradeYear
    ) {
        PageBean<Class> pb = classService.paginationQuery(pageNum, pageSize, collegedId, gradeYear);
        return Result.success(pb);
    }
}
