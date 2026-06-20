package com.anxinban.entity;


/**
 * ElderUser 实体类 — 老人档案表（elder_user）。
 * 核心实体，存储老人基本信息、当前健康状态、摄像头授权状态。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "elder_user")
public class ElderUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 主键，自增 */
    private Long id;

    @Column(name = "elder_id", nullable = false, unique = true)
    /** 老人业务编号，唯一 */
    private String elderId;

    @Column(nullable = false)
    /** 姓名 */
    private String name;

    /** 年龄 */
    private Integer age;

    /** 性别：男 / 女 */
    private String gender;

    /** 地址 */
    private String address;

    @Column(name = "building")
    /** 楼栋 */
    private String building;

    @Column(name = "room")
    /** 房号 */
    private String room;

    @Column(name = "phone")
    /** 老人电话 */
    private String phone;

    @Column(name = "password", nullable = false)
    /** 登录密码（生产需加密） */
    private String password;

    @Column(name = "health_status")
    /** 健康风险等级：normal / warning / danger */
    private String healthStatus;

    @Column(name = "health_status_text")
    /** 健康风险中文展示文本 */
    private String healthStatusText;

    @Column(name = "health_note")
    /** 健康备注 / 标签（逗号分隔） */
    private String healthNote;

    @Column(name = "family_phone")
    /** 家属电话 */
    private String familyPhone;

    @Column(name = "contact_name")
    /** 紧急联系人姓名 */
    private String contactName;

    @Column(name = "contact_phone")
    /** 紧急联系人电话 */
    private String contactPhone;

    @Column(name = "community_id")
    /** 所属社区/机构 ID */
    private String communityId;

    /** 头像 URL */
    private String avatar;

    @Column(name = "has_camera")
    /** 是否安装摄像头：0=否，1=是 */
    private Boolean hasCamera = false;

    @Column(name = "camera_auth_until")
    /** 摄像头授权到期时间（epoch 毫秒），0 表示未授权 */
    private Long cameraAuthUntil = 0L;

    @Column(name = "camera_auth_type")
    /** 已授权摄像头类型：door / living / bedroom */
    private String cameraAuthType;

    @Column(name = "camera_pending")
    /** 是否有待审批的监控申请 */
    private Boolean cameraPending = false;

    @Column(name = "last_online")
    /** 最后在线时间 */
    private LocalDateTime lastOnline;

    @Column(name = "created_at")
    /** 创建时间 */
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    /** 更新时间 */
    private LocalDateTime updatedAt;

    // Getters and Setters
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
     * 获取年龄。
     *
     * @return 年龄
     */
    public Integer getAge() { return age; }
    /**
     * 设置年龄。
     *
     * @param age 年龄
     */
    public void setAge(Integer age) { this.age = age; }

    /**
     * 获取性别。
     *
     * @return 性别
     */
    public String getGender() { return gender; }
    /**
     * 设置性别。
     *
     * @param gender 性别
     */
    public void setGender(String gender) { this.gender = gender; }

    /**
     * 获取地址。
     *
     * @return 地址
     */
    public String getAddress() { return address; }
    /**
     * 设置地址。
     *
     * @param address 地址
     */
    public void setAddress(String address) { this.address = address; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getBuilding() { return building; }
    /**
     * 设置字段含义待补充。
     *
     * @param building 字段含义待补充
     */
    public void setBuilding(String building) { this.building = building; }

    /**
     * 获取房号。
     *
     * @return 房号
     */
    public String getRoom() { return room; }
    /**
     * 设置房号。
     *
     * @param room 房号
     */
    public void setRoom(String room) { this.room = room; }

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
     * 获取状态标识。
     *
     * @return 状态标识
     */
    public String getHealthStatus() { return healthStatus; }
    /**
     * 设置状态标识。
     *
     * @param healthStatus 状态标识
     */
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }

    /**
     * 获取状态标识。
     *
     * @return 状态标识
     */
    public String getHealthStatusText() { return healthStatusText; }
    /**
     * 设置状态标识。
     *
     * @param healthStatusText 状态标识
     */
    public void setHealthStatusText(String healthStatusText) { this.healthStatusText = healthStatusText; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getHealthNote() { return healthNote; }
    /**
     * 设置字段含义待补充。
     *
     * @param healthNote 字段含义待补充
     */
    public void setHealthNote(String healthNote) { this.healthNote = healthNote; }

    /**
     * 获取手机号。
     *
     * @return 手机号
     */
    public String getFamilyPhone() { return familyPhone; }
    /**
     * 设置手机号。
     *
     * @param familyPhone 手机号
     */
    public void setFamilyPhone(String familyPhone) { this.familyPhone = familyPhone; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

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
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public Boolean getHasCamera() { return hasCamera; }
    /**
     * 设置字段含义待补充。
     *
     * @param hasCamera 字段含义待补充
     */
    public void setHasCamera(Boolean hasCamera) { this.hasCamera = hasCamera; }

    /**
     * 获取摄像头授权到期时间。
     *
     * @return 摄像头授权到期时间
     */
    public Long getCameraAuthUntil() { return cameraAuthUntil; }
    /**
     * 设置摄像头授权到期时间（epoch 毫秒）。
     *
     * @param cameraAuthUntil 摄像头授权到期时间
     */
    public void setCameraAuthUntil(Long cameraAuthUntil) { this.cameraAuthUntil = cameraAuthUntil; }

    public String getCameraAuthType() { return cameraAuthType; }
    public void setCameraAuthType(String cameraAuthType) { this.cameraAuthType = cameraAuthType; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public Boolean getCameraPending() { return cameraPending; }
    /**
     * 设置字段含义待补充。
     *
     * @param cameraPending 字段含义待补充
     */
    public void setCameraPending(Boolean cameraPending) { this.cameraPending = cameraPending; }

    /**
     * 获取是否在线。
     *
     * @return 是否在线
     */
    public LocalDateTime getLastOnline() { return lastOnline; }
    /**
     * 设置是否在线。
     *
     * @param lastOnline 是否在线
     */
    public void setLastOnline(LocalDateTime lastOnline) { this.lastOnline = lastOnline; }

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
