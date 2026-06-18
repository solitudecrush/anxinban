package com.anxinban.dto;

/**
 * 用户登录请求参数封装类。
 *
 * <p>封装前端登录界面提交的手机号、密码及用户类型信息。
 * 由 {@link com.anxinban.controller.AuthController} 接收后交给
 * {@link com.anxinban.service.AuthService} 进行身份校验。</p>
 */
public class LoginRequest {

    /** 用户手机号，作为登录账号使用，示例：13800138000 */
    private String phone;

    /** 用户密码，明文或前端加密后的密码，由业务层决定加密策略 */
    private String password;

    /**
     * 用户类型，用于区分不同角色。
     *
     * <p>常见取值：</p>
     * <ul>
     *   <li>elder：老人用户</li>
     *   <li>family：家属用户</li>
     *   <li>staff：工作人员</li>
     * </ul>
     */
    private String userType;

    /**
     * 获取登录手机号。
     *
     * @return 手机号
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置登录手机号。
     *
     * @param phone 手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取登录密码。
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置登录密码。
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取用户类型。
     *
     * @return 用户类型
     */
    public String getUserType() {
        return userType;
    }

    /**
     * 设置用户类型。
     *
     * @param userType 用户类型
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }
}
