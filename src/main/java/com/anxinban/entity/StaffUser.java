package com.anxinban.entity;


/**
 * StaffUser 实体类 — 工作人员表（staff_user）。
 * 存储社区管理人员账号信息。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff_user")
public class StaffUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 主键，自增 */
    private Long id;

    @Column(name = "staff_id", nullable = false, unique = true)
    /** 工作人员业务编号 */
    private String staffId;

    @Column(nullable = false, unique = true)
    /** 登录账号，唯一 */
    private String username;

    @Column(nullable = false)
    /** 真实姓名 */
    private String name;

    @Column(nullable = false, unique = true)
    /** 联系电话 */
    private String phone;

    @Column(nullable = false)
    /** 登录密码（生产需加密） */
    private String password;

    /** 角色：社区管理员 / 健康护理员 / 设备维修员 */
    private String role;

    @Column(name = "community_id")
    /** 所属社区/机构 ID */
    private String communityId;

    /** 头像 URL */
    private String avatar;

    @Column(name = "created_at")
    /** 创建时间 */
    private LocalDateTime createdAt;

    @Column(name = "update_time")
    /** 更新时间 */
    private LocalDateTime updatedAt;

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public Long getId() { return id; }
    /**
     * 设置唯一标识，主键。
     *
     * @param id 唯一标识，主键
     */
    public void setId(Long id) { this.id = id; }

    /**
     * 获取关联工作人员 ID。
     *
     * @return 关联工作人员 ID
     */
    public String getStaffId() { return staffId; }
    /**
     * 设置关联工作人员 ID。
     *
     * @param staffId 关联工作人员 ID
     */
    public void setStaffId(String staffId) { this.staffId = staffId; }

    /**
     * 获取名称。
     *
     * @return 名称
     */
    public String getUsername() { return username; }
    /**
     * 设置名称。
     *
     * @param username 名称
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * 获取名称。
     *
     * @return 名称
     */
    public String getName() { return name; }
    /**
     * 设置名称。
     *
     * @param name 名称
     */
    public void setName(String name) { this.name = name; }

    /**
     * 获取手机号。
     *
     * @return 手机号
     */
    public String getPhone() { return phone; }
    /**
     * 设置手机号。
     *
     * @param phone 手机号
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * 获取密码。
     *
     * @return 密码
     */
    public String getPassword() { return password; }
    /**
     * 设置密码。
     *
     * @param password 密码
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * 获取角色。
     *
     * @return 角色
     */
    public String getRole() { return role; }
    /**
     * 设置角色。
     *
     * @param role 角色
     */
    public void setRole(String role) { this.role = role; }

    /**
     * 获取所属社区/机构 ID。
     *
     * @return 所属社区/机构 ID
     */
    public String getCommunityId() { return communityId; }
    /**
     * 设置所属社区/机构 ID。
     *
     * @param communityId 所属社区/机构 ID
     */
    public void setCommunityId(String communityId) { this.communityId = communityId; }

    /**
     * 获取头像 URL。
     *
     * @return 头像 URL
     */
    public String getAvatar() { return avatar; }
    /**
     * 设置头像 URL。
     *
     * @param avatar 头像 URL
     */
    public void setAvatar(String avatar) { this.avatar = avatar; }

    /**
     * 获取创建时间。
     *
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * 设置创建时间。
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * 获取更新时间。
     *
     * @return 更新时间
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    /**
     * 设置更新时间。
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
