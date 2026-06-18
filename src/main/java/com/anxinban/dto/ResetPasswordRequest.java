package com.anxinban.dto;


/**
 * ResetPassword 请求参数封装类，用于接收前端传入的数据。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

public class ResetPasswordRequest {
    /** 手机号 */
    private String phone;
    /** 状态码 */
    private String verifyCode;
    /** 密码 */
    private String newPassword;

    /**
     * 获取手机号。
     *
     * @return 手机号
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置手机号。
     *
     * @param phone 手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取状态码。
     *
     * @return 状态码
     */
    public String getVerifyCode() {
        return verifyCode;
    }

    /**
     * 设置状态码。
     *
     * @param verifyCode 状态码
     */
    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    /**
     * 获取密码。
     *
     * @return 密码
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * 设置密码。
     *
     * @param newPassword 密码
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
