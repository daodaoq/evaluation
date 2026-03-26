package com.project.evaluation.exception;

import com.project.evaluation.entity.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
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
        return Result.error(msg.isBlank() ? "请求参数不合法" : msg);
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
        return Result.error(msg.isBlank() ? "请求参数不合法" : msg);
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
        return Result.error(msg.isBlank() ? "请求参数不合法" : msg);
    }

    /**
     * 缺少必要请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handleMissingParam(MissingServletRequestParameterException e) {
        return Result.error("缺少请求参数: " + e.getParameterName());
    }

    /**
     * 请求体解析失败（JSON 格式错误等）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleMessageNotReadable(HttpMessageNotReadableException e) {
        return Result.error("请求体格式错误");
    }

    /**
     * 请求方法不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return Result.error("不支持的请求方法: " + e.getMethod());
    }

    /**
     * 业务参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgument(IllegalArgumentException e) {
        return Result.error(e.getMessage() == null ? "请求参数不合法" : e.getMessage());
    }

    /**
     * 业务状态异常
     */
    @ExceptionHandler(IllegalStateException.class)
    public Result<?> handleIllegalState(IllegalStateException e) {
        return Result.error(e.getMessage() == null ? "操作失败" : e.getMessage());
    }

    /**
     * 权限不足
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> handleAccessDenied(AccessDeniedException e) {
        return Result.error("您的权限不足");
    }

    /**
     * 上传文件过大
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        return Result.error("上传文件过大");
    }

    /**
     * 未捕获异常兜底
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("全局异常: method={}, uri={}, msg={}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
        return Result.error("系统异常，请稍后重试");
    }
}