package com.project.evaluation.controller;

import com.project.evaluation.entity.PageBean;
import com.project.evaluation.entity.Result;
import com.project.evaluation.service.ClassEvaluationScoreService;
import com.project.evaluation.vo.ClassScore.ClassEvaluationScoreRowVO;
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

@RestController
@RequestMapping("/class-evaluation-scores")
public class ClassEvaluationScoreController {

    @Autowired
    private ClassEvaluationScoreService classEvaluationScoreService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:class-score:menu')")
    @CrossOrigin
    public Result<PageBean<ClassEvaluationScoreRowVO>> list(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam Long periodId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String studentNo) {
        return Result.success(classEvaluationScoreService.page(pageNum, pageSize, periodId, classId, studentNo));
    }

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('sys:class-score:menu')")
    @CrossOrigin
    public ResponseEntity<byte[]> export(
            @RequestParam Long periodId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String studentNo) {
        byte[] data = classEvaluationScoreService.exportExcel(periodId, classId, studentNo);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        String encoded = URLEncoder.encode("班级综测成绩.xlsx", StandardCharsets.UTF_8).replace("+", "%20");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
