package com.project.evaluation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统用户（教师/管理员等），与表 sys_user 对应。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户主键 */
    private Integer id;

    /** 登录账号（学号或工号） */
    private String studentId;

    /** 密码密文（含 Spring Security 编码前缀，如 {bcrypt}） */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 所属学院 id */
    private Integer collegeId;

    /** 所属班级 id（可空） */
    private Integer classId;

    /** 账号状态：1 正常，0 停用 */
    private Integer status;

    /** 记录创建时间 */
    private LocalDateTime createTime;
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;
}
