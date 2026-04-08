package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.ClassEvaluationScoreService;
import com.project.evaluation.vo.ClassScore.ClassEvaluationScoreRowVO;
import com.project.evaluation.vo.ClassScore.ClassUnsubmittedRowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/class-evaluation-scores")
public class ClassEvaluationScoreController {

    @Autowired
    private ClassEvaluationScoreService classEvaluationScoreService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:class-score:menu')")
    public Result<PageBean<ClassEvaluationScoreRowVO>> list(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) List<Long> periodIds,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String totalSortOrder) {
        List<Long> pids = periodIds == null ? Collections.emptyList() : periodIds;
        return Result.success(classEvaluationScoreService.page(pageNum, pageSize, pids, classId, studentNo, totalSortOrder));
    }

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('sys:class-score:menu')")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) List<Long> periodIds,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String totalSortOrder) {
        List<Long> pids = periodIds == null ? Collections.emptyList() : periodIds;
        byte[] data = classEvaluationScoreService.exportExcel(pids, classId, studentNo, totalSortOrder);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        String encoded = URLEncoder.encode("班级综测成绩.xlsx", StandardCharsets.UTF_8).replace("+", "%20");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @GetMapping("/unsubmitted/list")
    @PreAuthorize("hasAuthority('sys:class-score:menu')")
    public Result<PageBean<ClassUnsubmittedRowVO>> unsubmittedList(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) List<Long> periodIds,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String studentNo) {
        List<Long> pids = periodIds == null ? Collections.emptyList() : periodIds;
        return Result.success(classEvaluationScoreService.pageUnsubmitted(pageNum, pageSize, pids, classId, studentNo));
    }

    @GetMapping("/unsubmitted/export")
    @PreAuthorize("hasAuthority('sys:class-score:menu')")
    public ResponseEntity<byte[]> exportUnsubmitted(
            @RequestParam(required = false) List<Long> periodIds,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String studentNo) {
        List<Long> pids = periodIds == null ? Collections.emptyList() : periodIds;
        byte[] data = classEvaluationScoreService.exportUnsubmittedExcel(pids, classId, studentNo);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        String encoded = URLEncoder.encode("未提交名单.xlsx", StandardCharsets.UTF_8).replace("+", "%20");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
