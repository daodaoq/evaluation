package com.project.evaluation.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 API 响应包装类。
 *
 * @param <T> data 字段承载的业务数据类型
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result <T>{

    /** 业务状态码：0 成功，非 0 失败（具体含义与前端约定） */
    private Integer code;

    /** 提示信息（成功文案或错误原因） */
    private String message;

    /** 业务数据载荷；失败时多为 null */
    private T data;

    /**
     * 快速返回操作成功响应结果（带响应数据）
     * @param data
     * @return
     * @param <E>
     */
    public static <E>Result<E> success(E data)
    {
         return new Result<>(0,"操作成功",data);
    }
    /**
     * 快速返回操作成功响应结果
     * @return
     */
    public static Result success() {
        return new Result(0, "操作成功", null);
    }

    /**
     * 快速返回操作失败响应结果
     * @param message
     * @return
     */
    public static  Result error(String message) {
        return new Result(1, message, null);
    }

}
