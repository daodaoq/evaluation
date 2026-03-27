package com.project.evaluation.service;

import com.project.evaluation.entity.AcademicScore;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.vo.AcademicScore.AddAcademicScoreReq;
import com.project.evaluation.vo.AcademicScore.MyAcademicScoreVO;
import com.project.evaluation.vo.AcademicScore.UpdateAcademicScoreReq;
import org.springframework.web.multipart.MultipartFile;

public interface AcademicScoreService {
    PageBean<AcademicScore> pageQuery(Integer pageNum, Integer pageSize, Long periodId, String studentNo, String className, String studentName);

    void add(AddAcademicScoreReq req);

    void update(Long id, UpdateAcademicScoreReq req);

    void delete(Long id);

    int importExcel(Long periodId, MultipartFile file);

    MyAcademicScoreVO getMyScore(Long periodId);
}
