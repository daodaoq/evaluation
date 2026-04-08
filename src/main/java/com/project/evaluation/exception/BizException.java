package com.project.evaluation.exception;

/**
 * 统一业务异常。
 */
public class BizException extends RuntimeException {

    private final int code;

    public BizException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.code = errorCode.getCode();
    }

    public BizException(ErrorCode errorCode, String message) {
        super(message == null || message.isBlank() ? errorCode.getDefaultMessage() : message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}

