package com.anxinban.dto;

/**
 * 用户登录成功后的响应数据封装类。
 *
 * <p>包含访问令牌、刷新令牌及当前登录用户的基本信息，
 * 由 {@link com.anxinban.service.AuthService} 构造后返回给前端，
 * 前端用于保存登录态并渲染用户界面。</p>
 */
public class LoginResponse {

    /** 访问令牌（Access Token），用于后续请求的身份认证，建议设置较短有效期 */
    private String accessToken;

    /** 刷新令牌（Refresh Token），用于 accessToken 过期后换取新的令牌，建议设置较长有效期 */
    private String refreshToken;

    /** 当前登录用户的唯一标识 */
    private String userId;

    /** 当前登录用户的姓名 */
    private String name;

    /** 当前登录用户的手机号 */
    private String phone;

    /** 当前登录用户的角色，如 elder、family、staff 等 */
    private String role;

    /** 当前登录用户所属的社区/机构 ID，工作人员或老人可能关联社区 */
    private String communityId;

    /** 当前登录用户的头像 URL */
    private String avatar;

    /**
     * 获取访问令牌。
     *
     * @return 访问令牌
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * 设置访问令牌。
     *
     * @param accessToken 访问令牌
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * 获取刷新令牌。
     *
     * @return 刷新令牌
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * 设置刷新令牌。
     *
     * @param refreshToken 刷新令牌
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * 获取用户 ID。
     *
     * @return 用户 ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户 ID。
     *
     * @param userId 用户 ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取用户姓名。
     *
     * @return 用户姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置用户姓名。
     *
     * @param name 用户姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取用户手机号。
     *
     * @return 手机号
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置用户手机号。
     *
     * @param phone 手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取用户角色。
     *
     * @return 角色
     */
    public String getRole() {
        return role;
    }

    /**
     * 设置用户角色。
     *
     * @param role 角色
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * 获取社区/机构 ID。
     *
     * @return 社区 ID
     */
    public String getCommunityId() {
        return communityId;
    }

    /**
     * 设置社区/机构 ID。
     *
     * @param communityId 社区 ID
     */
    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    /**
     * 获取用户头像 URL。
     *
     * @return 头像 URL
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置用户头像 URL。
     *
     * @param avatar 头像 URL
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
