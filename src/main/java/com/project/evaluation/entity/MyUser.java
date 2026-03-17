package com.project.evaluation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Integer id;

    /**
     * 用户名（学号）
     */
    private String studentId;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户真实姓名
     */
    private String realName;

    /**
     * 学院 id
     */
    private Integer collegeId;

    /**
     * 班级 id
     */
    private Integer classId;

    /**
     * 用户状态
     */
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
