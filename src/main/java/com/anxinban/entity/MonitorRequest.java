package com.anxinban.entity;


/**
 * Monitor 请求参数封装类，用于接收前端传入的数据。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monitor_request")
public class MonitorRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "request_id", nullable = false, unique = true)
    /** 唯一标识，主键 */
    private String requestId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "staff_id", nullable = false)
    /** 关联工作人员 ID */
    private String staffId;

    @Column(name = "staff_name")
    /** 名称 */
    private String staffName;

    @Column(name = "staff_phone")
    /** 手机号 */
    private String staffPhone;

    @Column(name = "reason")
    /** 字段含义待补充 */
    private String reason;

    /** 状态标识 */
    private String status;

    @Column(name = "approved_at")
    /** 字段含义待补充 */
    private Long approvedAt;

    @Column(name = "expired_at")
    /** 字段含义待补充 */
    private LocalDateTime expiredAt;

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
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getRequestId() { return requestId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param requestId 唯一标识，主键
     */
    public void setRequestId(String requestId) { this.requestId = requestId; }

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
    public String getStaffName() { return staffName; }
    /**
     * 设置名称。
     *
     * @param staffName 名称
     */
    public void setStaffName(String staffName) { this.staffName = staffName; }

    /**
     * 获取手机号。
     *
     * @return 手机号
     */
    public String getStaffPhone() { return staffPhone; }
    /**
     * 设置手机号。
     *
     * @param staffPhone 手机号
     */
    public void setStaffPhone(String staffPhone) { this.staffPhone = staffPhone; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getReason() { return reason; }
    /**
     * 设置字段含义待补充。
     *
     * @param reason 字段含义待补充
     */
    public void setReason(String reason) { this.reason = reason; }

    /**
     * 获取状态标识。
     *
     * @return 状态标识
     */
    public String getStatus() { return status; }
    /**
     * 设置状态标识。
     *
     * @param status 状态标识
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public Long getApprovedAt() { return approvedAt; }
    /**
     * 设置字段含义待补充。
     *
     * @param approvedAt 字段含义待补充
     */
    public void setApprovedAt(Long approvedAt) { this.approvedAt = approvedAt; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public LocalDateTime getExpiredAt() { return expiredAt; }
    /**
     * 设置字段含义待补充。
     *
     * @param expiredAt 字段含义待补充
     */
    public void setExpiredAt(LocalDateTime expiredAt) { this.expiredAt = expiredAt; }

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
