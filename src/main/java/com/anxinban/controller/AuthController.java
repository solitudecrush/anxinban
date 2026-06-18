package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.LoginRequest;
import com.anxinban.dto.LoginResponse;
import com.anxinban.dto.RegisterRequest;
import com.anxinban.dto.ResetPasswordRequest;
import com.anxinban.dto.StaffDto;
import com.anxinban.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证授权控制器。
 *
 * <p>功能说明：处理用户登录、注册、密码重置、登出以及当前登录用户信息查询等认证相关接口。</p>
 *
 * <p>对应前端模块：登录页 / 注册页 / 忘记密码 / 个人中心。</p>
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * 认证服务，负责登录、注册、密码重置等核心业务。
     */
    private final AuthService authService;

    /**
     * 构造方法，注入认证服务。
     *
     * @param authService 认证服务实例
     */
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录接口。
     *
     * @param request 登录请求，包含手机号、密码及用户类型
     * @return 登录成功返回登录响应（含 token 等）；失败返回 401 错误响应
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        // 登录失败时返回统一的 401 错误提示
        if (response == null) {
            return ApiResponse.error(401, "手机号或密码错误");
        }
        return ApiResponse.success(response);
    }

    /**
     * 用户注册接口。
     *
     * @param request 注册请求，包含手机号、密码、姓名、验证码等
     * @return 注册成功返回登录响应；若手机号已被注册则返回 409 冲突响应
     */
    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        // 注册失败通常是因为手机号重复
        if (response == null) {
            return ApiResponse.error(409, "手机号已被注册");
        }
        return ApiResponse.success(response);
    }

    /**
     * 重置密码接口。
     *
     * @param request 重置密码请求，包含手机号与新密码
     * @return 重置成功返回空数据成功响应；用户不存在返回 404 错误响应
     */
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean success = authService.resetPassword(request.getPhone(), request.getNewPassword());
        if (!success) {
            return ApiResponse.error(404, "用户不存在");
        }
        return ApiResponse.success(null);
    }

    /**
     * 用户登出接口。
     *
     * @return 登出成功响应
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        // 当前实现为无状态登出，直接返回成功即可
        return ApiResponse.success(null);
    }

    /**
     * 根据手机号查询当前登录用户信息。
     *
     * @param phone 用户手机号
     * @return 用户信息；若用户不存在则返回 404 错误响应
     */
    @GetMapping("/me")
    public ApiResponse<StaffDto> getCurrentUser(@RequestParam String phone) {
        StaffDto dto = authService.getStaffByPhone(phone);
        if (dto == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        return ApiResponse.success(dto);
    }
}
