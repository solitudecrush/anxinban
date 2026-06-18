package com.anxinban.dto;


/**
 * Staff 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

public class StaffDto {
    /** 关联工作人员 ID */
    private String staffId;
    /** 名称 */
    private String username;
    /** 名称 */
    private String name;
    /** 手机号 */
    private String phone;
    /** 角色 */
    private String role;
    /** 所属社区/机构 ID */
    private String communityId;
    /** 头像 URL */
    private String avatar;

    /**
     * 获取关联工作人员 ID。
     *
     * @return 关联工作人员 ID
     */
    public String getStaffId() {
        return staffId;
    }

    /**
     * 设置关联工作人员 ID。
     *
     * @param staffId 关联工作人员 ID
     */
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    /**
     * 获取名称。
     *
     * @return 名称
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置名称。
     *
     * @param username 名称
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取名称。
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称。
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

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
     * 获取角色。
     *
     * @return 角色
     */
    public String getRole() {
        return role;
    }

    /**
     * 设置角色。
     *
     * @param role 角色
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * 获取所属社区/机构 ID。
     *
     * @return 所属社区/机构 ID
     */
    public String getCommunityId() {
        return communityId;
    }

    /**
     * 设置所属社区/机构 ID。
     *
     * @param communityId 所属社区/机构 ID
     */
    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    /**
     * 获取头像 URL。
     *
     * @return 头像 URL
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置头像 URL。
     *
     * @param avatar 头像 URL
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
