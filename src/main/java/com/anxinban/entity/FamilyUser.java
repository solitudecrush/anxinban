package com.anxinban.entity;


/**
 * FamilyUser 实体类，对应数据库中的一张业务表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "family_user")
public class FamilyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "family_id", nullable = false, unique = true)
    /** 关联家属用户 ID */
    private String familyId;

    @Column(nullable = false)
    /** 名称 */
    private String name;

    @Column(nullable = false, unique = true)
    /** 手机号 */
    private String phone;

    @Column(nullable = false)
    /** 密码 */
    private String password;

    @Column(name = "elder_id")
    /** 关联老人用户 ID */
    private String elderId;

    /** 关系描述 */
    private String relation;

    /** 头像 URL */
    private String avatar;

    @Column(name = "created_at")
    /** 记录创建时间 */
    private LocalDateTime createdAt;

    @Column(name = "update_time")
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;

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
     * 获取关联家属用户 ID。
     *
     * @return 关联家属用户 ID
     */
    public String getFamilyId() { return familyId; }
    /**
     * 设置关联家属用户 ID。
     *
     * @param familyId 关联家属用户 ID
     */
    public void setFamilyId(String familyId) { this.familyId = familyId; }

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
     * 获取关联老人用户 ID。
     *
     * @return 关联老人用户 ID
     */
    public String getElderId() { return elderId; }
    /**
     * 设置关联老人用户 ID。
     *
     * @param elderId 关联老人用户 ID
     */
    public void setElderId(String elderId) { this.elderId = elderId; }

    /**
     * 获取关系描述。
     *
     * @return 关系描述
     */
    public String getRelation() { return relation; }
    /**
     * 设置关系描述。
     *
     * @param relation 关系描述
     */
    public void setRelation(String relation) { this.relation = relation; }

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
     * 获取记录创建时间。
     *
     * @return 记录创建时间
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * 设置记录创建时间。
     *
     * @param createdAt 记录创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * 获取记录最后更新时间。
     *
     * @return 记录最后更新时间
     */
    public LocalDateTime getUpdateTime() { return updateTime; }
    /**
     * 设置记录最后更新时间。
     *
     * @param updateTime 记录最后更新时间
     */
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
