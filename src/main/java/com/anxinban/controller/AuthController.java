package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.LoginByClientRequest;
import com.anxinban.dto.LoginRequest;
import com.anxinban.dto.LoginResponse;
import com.anxinban.dto.RegisterRequest;
import com.anxinban.dto.ResetPasswordRequest;
import com.anxinban.dto.StaffDto;
import com.anxinban.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证授权控制器。
 *
 * <p>功能说明：处理用户登录、注册、密码重置、登出以及当前登录用户信息查询等认证相关接口。</p>
 *
 * <p>登录按客户端方向分离：</p>
 * <ul>
 *   <li>{@code POST /api/auth/login/web} — Web 端工作人员登录</li>
 *   <li>{@code POST /api/auth/login/app} — App 端家属登录</li>
 *   <li>{@code POST /api/auth/login} — 旧版登录（保留兼容）</li>
 * </ul>
 *
 * <p>对应前端模块：登录页 / 注册页 / 忘记密码 / 个人中心。</p>
 *
 * @author 安心伴开发团队
 * @since 0.0.2-SNAPSHOT
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ==================== 新版登录端点 ====================

    /**
     * Web 端工作人员登录接口。
     *
     * <p>前端只需传入手机号和密码，后端自动以 staff 身份校验。</p>
     *
     * @param request 登录请求，包含手机号和密码
     * @return 登录成功返回 LoginResponse；失败返回 401
     */
    @PostMapping("/login/web")
    public ApiResponse<LoginResponse> staffLogin(@RequestBody LoginByClientRequest request) {
        LoginResponse response = authService.staffLogin(request.getPhone(), request.getPassword());
        if (response == null) {
            return ApiResponse.error(401, "手机号或密码错误");
        }
        return ApiResponse.success(response);
    }

    /**
     * App 端家属登录接口。
     *
     * <p>前端只需传入手机号和密码，后端自动以 family 身份校验。</p>
     *
     * @param request 登录请求，包含手机号和密码
     * @return 登录成功返回 LoginResponse；失败返回 401
     */
    @PostMapping("/login/app")
    public ApiResponse<LoginResponse> familyLogin(@RequestBody LoginByClientRequest request) {
        LoginResponse response = authService.familyLogin(request.getPhone(), request.getPassword());
        if (response == null) {
            return ApiResponse.error(401, "手机号或密码错误");
        }
        return ApiResponse.success(response);
    }

    // ==================== 旧版登录端点（保留兼容） ====================

    /**
     * 旧版用户登录接口（保留向后兼容）。
     *
     * <p>根据 userType 转发：staff → 工作人员登录，family → 家属登录，
     * elder 不再支持登录。</p>
     *
     * @param request 登录请求，包含手机号、密码及用户类型
     * @return 登录成功返回 LoginResponse；失败返回 401
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        if ("elder".equals(request.getUserType())) {
            return ApiResponse.error(403, "老人用户不支持登录");
        }
        LoginResponse response = authService.login(request);
        if (response == null) {
            return ApiResponse.error(401, "手机号或密码错误");
        }
        return ApiResponse.success(response);
    }

    // ==================== 注册 ====================

    /**
     * 用户注册接口。
     *
     * <p>仅支持 staff 和 family 注册，老人不支持自行注册。</p>
     *
     * @param request 注册请求，包含手机号、密码、姓名、验证码等
     * @return 注册成功返回 LoginResponse；若手机号已被注册返回 409；不支持的类型返回 403
     */
    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@RequestBody RegisterRequest request) {
        if ("elder".equals(request.getUserType())) {
            return ApiResponse.error(403, "老人用户不支持注册");
        }
        LoginResponse response = authService.register(request);
        if (response == null) {
            return ApiResponse.error(409, "手机号已被注册");
        }
        return ApiResponse.success(response);
    }

    // ==================== 密码重置 ====================

    /**
     * 重置密码接口。
     *
     * <p>仅支持 staff 和 family 用户，老人不支持。</p>
     *
     * @param request 重置密码请求，包含手机号与新密码
     * @return 重置成功返回空数据成功响应；用户不存在返回 404
     */
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean success = authService.resetPassword(request.getPhone(), request.getNewPassword());
        if (!success) {
            return ApiResponse.error(404, "用户不存在");
        }
        return ApiResponse.success(null);
    }

    // ==================== 登出 ====================

    /**
     * 用户登出接口。
     *
     * @return 登出成功响应
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success(null);
    }

    // ==================== 当前用户信息 ====================

    /**
     * 根据手机号查询当前登录用户信息（支持 staff 和 family）。
     *
     * <p>优先查 staff 表，未找到再查 family 表。</p>
     *
     * @param phone 用户手机号
     * @return 用户信息；若用户不存在则返回 404
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
