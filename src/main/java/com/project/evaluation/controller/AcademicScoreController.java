package com.project.evaluation.controller;

import com.project.evaluation.entity.AcademicScore;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.AcademicScoreService;
import com.project.evaluation.vo.AcademicScore.AddAcademicScoreReq;
import com.project.evaluation.vo.AcademicScore.ClassOptionVO;
import com.project.evaluation.vo.AcademicScore.DeleteAcademicScoreReq;
import com.project.evaluation.vo.AcademicScore.MyAcademicScoreVO;
import com.project.evaluation.vo.AcademicScore.UpdateAcademicScoreReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/academic-scores")
public class AcademicScoreController {

    @Autowired
    private AcademicScoreService academicScoreService;

    /**
     * 智育管理页：班级下拉数据（学院 + 班级名称），无需再手输班级
     */
    @GetMapping("/classes")
    @PreAuthorize("hasAuthority('sys:academic:menu')")
    @CrossOrigin
    public Result<List<ClassOptionVO>> listClassesForAcademic() {
        return Result.success(academicScoreService.listClassOptionsForAcademic());
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:academic:menu')")
    @CrossOrigin
    public Result<PageBean<AcademicScore>> list(@RequestParam Integer pageNum,
                                                @RequestParam Integer pageSize,
                                                @RequestParam(required = false) List<Long> periodIds,
                                                @RequestParam(required = false) String studentNo,
                                                @RequestParam(required = false) List<String> classNames,
                                                @RequestParam(required = false) String studentName) {
        List<Long> pids = periodIds == null ? Collections.emptyList() : periodIds;
        List<String> cns = classNames == null ? Collections.emptyList() : classNames;
        return Result.success(academicScoreService.pageQuery(pageNum, pageSize, pids, studentNo, cns, studentName));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:academic:menu')")
    @CrossOrigin
    public Result<?> add(@RequestBody AddAcademicScoreReq req) {
        academicScoreService.add(req);
        return Result.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:academic:menu')")
    @CrossOrigin
    public Result<?> update(@PathVariable Long id, @RequestBody UpdateAcademicScoreReq req) {
        academicScoreService.update(id, req);
        return Result.success();
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:academic:menu')")
    @CrossOrigin
    public Result<?> delete(@RequestBody DeleteAcademicScoreReq req) {
        academicScoreService.delete(req.getId());
        return Result.success();
    }

    @PostMapping("/import-excel")
    @PreAuthorize("hasAuthority('sys:academic:menu')")
    @CrossOrigin
    public Result<?> importExcel(@RequestParam Long periodId, @RequestParam("file") MultipartFile file) {
        int cnt = academicScoreService.importExcel(periodId, file);
        return Result.success("导入成功，共 " + cnt + " 条");
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('sys:student:menu')")
    @CrossOrigin
    public Result<MyAcademicScoreVO> my(@RequestParam Long periodId) {
        return Result.success(academicScoreService.getMyScore(periodId));
    }
}
