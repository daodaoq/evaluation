package com.project.evaluation.exception;

/**
 * 统一业务错误码定义。
 */
public enum ErrorCode {
    PARAM_INVALID(4001, "请求参数不合法"),
    UNAUTHORIZED(4003, "未认证或登录已过期"),
    FORBIDDEN(4004, "权限不足"),
    RESOURCE_NOT_FOUND(4005, "资源不存在"),
    BIZ_CONFLICT(4009, "业务状态冲突"),
    SYSTEM_ERROR(5000, "系统异常，请稍后重试");

    private final int code;
    private final String defaultMessage;

    ErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public int getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}

