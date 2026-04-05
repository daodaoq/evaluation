package com.project.evaluation.vo.EvaluationApproval;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class EvaluationApplyItemVO {
    private Long applyId;
    private Long applyItemId;
    private Long studentId;
    private String studentNo;
    private String studentName;
    private Long collegeId;
    private String collegeName;
    private Long classId;
    private String className;
    private Long periodId;
    private String applyStatus;
    private String itemStatus;
    private Long ruleItemId;
    private String ruleItemName;
    /** 非细则项名称（细则项时通常为空） */
    private String customName;
    /** 学生填写的申报备注 */
    private String itemRemark;
    private BigDecimal score;
    private Long auditorId;
    private String auditorNo;
    private String auditorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applyCreateTime;
    /** 申报单最后更新时间（多次提交会变化） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applyUpdateTime;
    /** 本条申报项创建时间（可视为提交该条进入审批的时间） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime itemCreateTime;
    /** 学生上传的证明材料 */
    private List<EvaluationApplyMaterialVO> materials = new ArrayList<>();
}
