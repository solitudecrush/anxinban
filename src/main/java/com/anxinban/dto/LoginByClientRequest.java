package com.anxinban.dto;

/**
 * 按客户端类型区分的登录请求参数封装类。
 *
 * <p>Web 端（工作人员登录）和 App 端（家属登录）共用此 DTO，
 * 前端只需传入手机号和密码，无需指定用户类型，
 * 后端通过不同的端点路径自动确定查询哪张用户表。</p>
 *
 * <p>对应端点：</p>
 * <ul>
 *   <li>{@code POST /api/auth/login/web} — 工作人员登录，查询 staff 表</li>
 *   <li>{@code POST /api/auth/login/app} — 家属登录，查询 family_user 表</li>
 * </ul>
 *
 * @author 安心伴开发团队
 * @since 0.0.2-SNAPSHOT
 */
public class LoginByClientRequest {

    /** 用户手机号，作为登录账号 */
    private String phone;

    /** 用户密码，明文或前端加密后的密码 */
    private String password;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
