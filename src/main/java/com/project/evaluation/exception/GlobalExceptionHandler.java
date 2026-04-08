package com.project.evaluation.exception;

import com.project.evaluation.entity.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e, HttpServletRequest request) {
        log.warn("业务异常: method={}, uri={}, code={}, msg={}",
                request.getMethod(), request.getRequestURI(), e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * SQL 语法/字段异常（常见于库结构未执行迁移）
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    public Result<?> handleBadSqlGrammar(BadSqlGrammarException e, HttpServletRequest request) {
        log.error("SQL异常: method={}, uri={}, msg={}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        String raw = e.getMessage() == null ? "" : e.getMessage();
        if (raw.contains("Unknown column 'ai.source_type'")) {
            return Result.error("数据库缺少字段 source_type，请先执行 SQL：evaluation/sql/migrate_student_submit_schema_fix.sql");
        }
        if (raw.contains("Unknown column 'ri.score_mode'") || raw.contains("Unknown column 'ri.module_code'")) {
            return Result.error("数据库缺少规则项扩展字段，请先执行 SQL：evaluation/sql/migrate_student_submit_schema_fix.sql");
        }
        return Result.error("数据库结构与当前代码不匹配，请执行最新迁移 SQL 后重试");
    }

    /**
     * @Validated / @RequestParam 约束异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolation(ConstraintViolationException e) {
        String msg = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .filter(m -> m != null && !m.isBlank())
                .collect(Collectors.joining("；"));
        return Result.error(ErrorCode.PARAM_INVALID.getCode(), msg.isBlank() ? "请求参数不合法" : msg);
    }

    /**
     * @RequestBody + @Valid 校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .filter(m -> m != null && !m.isBlank())
                .collect(Collectors.joining("；"));
        return Result.error(ErrorCode.PARAM_INVALID.getCode(), msg.isBlank() ? "请求参数不合法" : msg);
    }

    /**
     * 表单绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String msg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .filter(m -> m != null && !m.isBlank())
                .collect(Collectors.joining("；"));
        return Result.error(ErrorCode.PARAM_INVALID.getCode(), msg.isBlank() ? "请求参数不合法" : msg);
    }

    /**
     * 缺少必要请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handleMissingParam(MissingServletRequestParameterException e) {
        return Result.error(ErrorCode.PARAM_INVALID.getCode(), "缺少请求参数: " + e.getParameterName());
    }

    /**
     * 请求体解析失败（JSON 格式错误等）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleMessageNotReadable(HttpMessageNotReadableException e) {
        return Result.error(ErrorCode.PARAM_INVALID.getCode(), "请求体格式错误");
    }

    /**
     * 请求方法不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return Result.error(ErrorCode.PARAM_INVALID.getCode(), "不支持的请求方法: " + e.getMethod());
    }

    /**
     * 业务参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgument(IllegalArgumentException e) {
        return Result.error(ErrorCode.PARAM_INVALID.getCode(), e.getMessage() == null ? "请求参数不合法" : e.getMessage());
    }

    /**
     * 业务状态异常
     */
    @ExceptionHandler(IllegalStateException.class)
    public Result<?> handleIllegalState(IllegalStateException e) {
        return Result.error(ErrorCode.BIZ_CONFLICT.getCode(), e.getMessage() == null ? "操作失败" : e.getMessage());
    }

    /**
     * 权限不足
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> handleAccessDenied(AccessDeniedException e) {
        return Result.error(ErrorCode.FORBIDDEN.getCode(), "您的权限不足");
    }

    /**
     * 上传文件过大
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        return Result.error(ErrorCode.PARAM_INVALID.getCode(), "上传文件过大");
    }

    /**
     * 未捕获异常兜底
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("全局异常: method={}, uri={}, msg={}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return Result.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统异常，请稍后重试");
    }
}