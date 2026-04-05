package com.project.evaluation.service;

import com.project.evaluation.entity.AcademicScore;
import com.project.evaluation.entity.PageBean;
import com.project.evaluation.vo.AcademicScore.AddAcademicScoreReq;
import com.project.evaluation.vo.AcademicScore.ClassOptionVO;
import com.project.evaluation.vo.AcademicScore.MyAcademicScoreVO;
import com.project.evaluation.vo.AcademicScore.UpdateAcademicScoreReq;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AcademicScoreService {
    PageBean<AcademicScore> pageQuery(Integer pageNum, Integer pageSize, List<Long> periodIds, String studentNo,
                                      List<String> classNames, String studentName);

    void add(AddAcademicScoreReq req);

    void update(Long id, UpdateAcademicScoreReq req);

    void delete(Long id);

    int importExcel(Long periodId, MultipartFile file);

    MyAcademicScoreVO getMyScore(Long periodId);

    /** 智育管理页：班级下拉（含学院名称，仅 sys:academic:menu） */
    List<ClassOptionVO> listClassOptionsForAcademic();
}
