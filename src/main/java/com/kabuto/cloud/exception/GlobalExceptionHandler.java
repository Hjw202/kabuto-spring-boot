package com.kabuto.cloud.exception;

import com.kabuto.cloud.common.enums.ResultCode;
import com.kabuto.cloud.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器
 *
 * <p><b>需求描述：</b>大王要求按照网上项目习惯封装异常捕获</p>
 * <p><b>解决方案：</b>使用 @RestControllerAdvice 统一捕获 Controller 层抛出的各类异常，
 * 统一包装为 R<T> 响应格式返回</p>
 * <p><b>原因说明：</b>前后端分离项目中，统一异常处理是标准做法。
 * 所有异常返回 HTTP 200，业务状态码放在 body 的 code 字段，
 * 便于前端拦截器统一处理。参考若依、芋道等开源项目设计。</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== 业务异常 ====================

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BizException.class)
    public R<Void> handleBizException(BizException e) {
        log.warn("[业务异常] code={}, msg={}", e.getCode(), e.getMessage());
        return R.fail(e.getResultCode(), e.getMessage());
    }

    // ==================== 参数校验异常 ====================

    /**
     * 处理参数校验失败（@Valid / @Validated 校验失败）
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValid(org.springframework.web.bind.MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("参数校验失败");
        log.warn("[参数校验失败] {}", message);
        return R.validation(message);
    }

    /**
     * 处理参数绑定失败（如 @RequestParam 类型不匹配）
     */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("参数绑定失败");
        log.warn("[参数绑定失败] {}", message);
        return R.badRequest(message);
    }

    /**
     * 处理请求体 JSON 格式错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("[请求体解析失败] {}", e.getMessage());
        return R.badRequest("请求体格式错误，请检查 JSON 格式");
    }

    /**
     * 处理缺少必填参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<Void> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("[缺少参数] 参数名: {}", e.getParameterName());
        return R.badRequest("缺少必填参数: " + e.getParameterName());
    }

    // ==================== 请求方式 / 路由异常 ====================

    /**
     * 处理请求方法不支持（如 POST 接口用了 GET）
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        log.warn("[请求方法不支持] 当前: {}, 支持: {}", e.getMethod(), e.getSupportedHttpMethods());
        return R.fail(ResultCode.METHOD_NOT_ALLOWED,
                "请求方法不支持: " + e.getMethod() + "，请使用: " + e.getSupportedHttpMethods());
    }

    /**
     * 处理接口不存在（需配置 spring.web.resources.add-mappings=false）
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public R<Void> handleNoHandlerFound(NoHandlerFoundException e) {
        log.warn("[接口不存在] {} {}", e.getHttpMethod(), e.getRequestURL());
        return R.notFound("接口不存在: " + e.getHttpMethod() + " " + e.getRequestURL());
    }

    // ==================== 其他常见异常 ====================

    /**
     * 处理非法参数
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public R<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("[非法参数] {}", e.getMessage());
        return R.badRequest(e.getMessage());
    }

    // ==================== 兜底异常 ====================

    /**
     * 处理 JWT 相关异常
     */
    @ExceptionHandler(io.jsonwebtoken.JwtException.class)
    public R<Void> handleJwtException(io.jsonwebtoken.JwtException e) {
        log.warn("[JWT 异常] {}", e.getMessage());
        return R.unauthorized("Token 无效或已过期");
    }

    /**
     * 处理未知系统异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("[系统异常] {}", e.getMessage(), e);
        return R.fail(ResultCode.ERROR, "服务器内部错误");
    }
}
