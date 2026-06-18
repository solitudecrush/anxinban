package com.anxinban.dto;

/**
 * API 统一响应封装类。
 *
 * <p>所有 RESTful 接口统一返回此结构，包含业务状态码、提示信息及实际数据。
 * 常用构造方式：</p>
 * <ul>
 *   <li>{@link #success(Object)}：操作成功，HTTP 语义对应 200；</li>
 *   <li>{@link #created(Object)}：资源创建成功，HTTP 语义对应 201；</li>
 *   <li>{@link #error(int, String)}：操作失败，由调用方指定错误码与错误信息。</li>
 * </ul>
 *
 * <p>注意：code 为业务状态码，与 HTTP 状态码概念不同，用于前端根据业务码做分支处理。</p>
 *
 * @param <T> 响应 payload 的数据类型
 */
public class ApiResponse<T> {

    /** 业务状态码，必填。成功时常用 200，创建成功时常用 201，错误时由具体错误码决定，示例：200 */
    private int code;

    /** 状态说明或错误提示信息，必填，示例：success 或 手机号不能为空 */
    private String message;

    /** 实际返回的数据，可能为 null（如删除操作或错误响应时），类型由泛型 T 决定 */
    private T data;

    public ApiResponse() {
    }

    /**
     * 全参构造方法。
     *
     * @param code    业务状态码
     * @param message 状态说明
     * @param data    响应数据
     */
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 构造成功响应。
     *
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return code 为 200、message 为 "success" 的响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /**
     * 构造创建成功响应。
     *
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return code 为 201、message 为 "created" 的响应对象
     */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "created", data);
    }

    /**
     * 构造错误响应。
     *
     * @param code    错误码
     * @param message 错误说明
     * @param <T>     响应数据类型
     * @return data 为 null 的错误响应对象
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /**
     * 获取状态码。
     *
     * @return 状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置状态码。
     *
     * @param code 状态码
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获取提示信息。
     *
     * @return 提示信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置提示信息。
     *
     * @param message 提示信息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取数据载荷。
     *
     * @return 数据载荷
     */
    public T getData() {
        return data;
    }

    /**
     * 设置数据载荷。
     *
     * @param data 数据载荷
     */
    public void setData(T data) {
        this.data = data;
    }
}
