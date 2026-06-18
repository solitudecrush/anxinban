package com.anxinban.entity;


/**
 * LocalAgent 实体类，对应数据库中的一张业务表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "local_agent")
public class LocalAgent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "agent_id", nullable = false, unique = true)
    /** 唯一标识，主键 */
    private String agentId;

    @Column(name = "agent_type")
    /** 类型标识 */
    private String agentType;

    /** 状态标识 */
    private String status;

    @Column(name = "last_heartbeat")
    /** 字段含义待补充 */
    private LocalDateTime lastHeartbeat;

    /** 字段含义待补充 */
    private String ip;

    @Column(name = "device_count")
    /** 字段含义待补充 */
    private Integer deviceCount;

    @Column(name = "connected_devices")
    /** 字段含义待补充 */
    private Integer connectedDevices;

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
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getAgentId() { return agentId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param agentId 唯一标识，主键
     */
    public void setAgentId(String agentId) { this.agentId = agentId; }

    /**
     * 获取类型标识。
     *
     * @return 类型标识
     */
    public String getAgentType() { return agentType; }
    /**
     * 设置类型标识。
     *
     * @param agentType 类型标识
     */
    public void setAgentType(String agentType) { this.agentType = agentType; }

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
    public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
    /**
     * 设置字段含义待补充。
     *
     * @param lastHeartbeat 字段含义待补充
     */
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getIp() { return ip; }
    /**
     * 设置字段含义待补充。
     *
     * @param ip 字段含义待补充
     */
    public void setIp(String ip) { this.ip = ip; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public Integer getDeviceCount() { return deviceCount; }
    /**
     * 设置字段含义待补充。
     *
     * @param deviceCount 字段含义待补充
     */
    public void setDeviceCount(Integer deviceCount) { this.deviceCount = deviceCount; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public Integer getConnectedDevices() { return connectedDevices; }
    /**
     * 设置字段含义待补充。
     *
     * @param connectedDevices 字段含义待补充
     */
    public void setConnectedDevices(Integer connectedDevices) { this.connectedDevices = connectedDevices; }

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
