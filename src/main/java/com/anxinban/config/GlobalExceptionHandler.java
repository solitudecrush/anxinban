package com.anxinban.config;

import com.anxinban.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理器。
 *
 * <p>确保所有未捕获的异常都以统一的 {@link ApiResponse} 格式返回，
 * 避免前端收到 Spring Boot 默认的 {@code {timestamp, status, error, path}} JSON 结构。
 * 所有 Handler 方法均返回 {@code ApiResponse<?>} 并标注 {@code @ResponseBody}。</p>
 *
 * <p>覆盖场景：</p>
 * <ul>
 *   <li>未知运行时异常 → code=500</li>
 *   <li>请求参数缺失/格式错误 → code=400</li>
 *   <li>不支持的 HTTP 方法 → code=405</li>
 * </ul>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 兜底异常处理 — 所有未被特定 Handler 捕获的异常统一返回 500。
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiResponse<?> handleException(Exception ex) {
        log.error("未捕获异常: {}", ex.getMessage(), ex);
        return ApiResponse.error(500, "服务器内部错误: " + ex.getMessage());
    }

    /**
     * 请求体不可读（JSON 格式错误等）→ 400。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ApiResponse<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("请求体解析失败: {}", ex.getMessage());
        return ApiResponse.error(400, "请求体格式错误，请检查 JSON 结构");
    }

    /**
     * 缺少必填请求参数 → 400。
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ApiResponse<?> handleMissingParam(MissingServletRequestParameterException ex) {
        log.warn("缺少请求参数: {}", ex.getMessage());
        return ApiResponse.error(400, "缺少必填参数: " + ex.getParameterName());
    }

    /**
     * 请求参数类型转换失败（如期望数字却传了字符串）→ 400。
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ApiResponse<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("参数类型不匹配: {} = {}", ex.getName(), ex.getValue());
        return ApiResponse.error(400, "参数类型错误: " + ex.getName());
    }

    /**
     * 不支持的 HTTP 方法 → 405。
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ApiResponse<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("不支持的 HTTP 方法: {}", ex.getMessage());
        return ApiResponse.error(405, "不支持的请求方法: " + ex.getMethod());
    }

    /**
     * 非法参数（业务层抛出的 IllegalArgumentException）→ 400。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ApiResponse<?> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("非法参数: {}", ex.getMessage());
        return ApiResponse.error(400, "参数错误: " + ex.getMessage());
    }
}
