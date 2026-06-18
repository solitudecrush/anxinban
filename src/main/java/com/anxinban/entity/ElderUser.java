package com.anxinban.entity;


/**
 * ElderUser 实体类，对应数据库中的一张业务表。
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
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "elder_id", nullable = false, unique = true)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(nullable = false)
    /** 名称 */
    private String name;

    /** 年龄 */
    private Integer age;

    /** 性别 */
    private String gender;

    /** 地址 */
    private String address;

    @Column(name = "building")
    /** 字段含义待补充 */
    private String building;

    @Column(name = "room_number")
    /** 房间名称/编号 */
    private String roomNumber;

    @Column(name = "phone")
    /** 手机号 */
    private String phone;

    @Column(name = "password", nullable = false)
    /** 密码 */
    private String password;

    @Column(name = "health_status")
    /** 状态标识 */
    private String healthStatus;

    @Column(name = "health_status_text")
    /** 状态标识 */
    private String healthStatusText;

    @Column(name = "health_note")
    /** 字段含义待补充 */
    private String healthNote;

    @Column(name = "family_phone")
    /** 手机号 */
    private String familyPhone;

    @Column(name = "community_id")
    /** 所属社区/机构 ID */
    private String communityId;

    /** 头像 URL */
    private String avatar;

    @Column(name = "has_camera")
    private Boolean hasCamera = false;

    @Column(name = "camera_auth_until")
    private Long cameraAuthUntil = 0L;

    @Column(name = "camera_pending")
    private Boolean cameraPending = false;

    @Column(name = "last_online")
    /** 是否在线 */
    private LocalDateTime lastOnline;

    @Column(name = "created_at")
    /** 记录创建时间 */
    private LocalDateTime createdAt;

    @Column(name = "update_time")
    /** 记录最后更新时间 */
    private LocalDateTime updateTime;

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
     * 获取房间名称/编号。
     *
     * @return 房间名称/编号
     */
    public String getRoomNumber() { return roomNumber; }
    /**
     * 设置房间名称/编号。
     *
     * @param roomNumber 房间名称/编号
     */
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

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
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public Long getCameraAuthUntil() { return cameraAuthUntil; }
    /**
     * 设置字段含义待补充。
     *
     * @param cameraAuthUntil 字段含义待补充
     */
    public void setCameraAuthUntil(Long cameraAuthUntil) { this.cameraAuthUntil = cameraAuthUntil; }

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
