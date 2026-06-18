package com.anxinban.entity;


/**
 * HomeControlLog 实体类，对应数据库中的一张业务表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "home_control_log")
public class HomeControlLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "control_id", nullable = false, unique = true)
    /** 唯一标识，主键 */
    private String controlId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "device_id")
    /** 关联设备 ID */
    private String deviceId;

    /** 字段含义待补充 */
    private String command;

    @Column(name = "source_agent")
    /** 年龄 */
    private String sourceAgent;

    /** 结果 */
    private String result;

    @Column(name = "created_at")
    /** 记录创建时间 */
    private LocalDateTime createdAt;

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
    public String getControlId() { return controlId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param controlId 唯一标识，主键
     */
    public void setControlId(String controlId) { this.controlId = controlId; }

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
     * 获取关联设备 ID。
     *
     * @return 关联设备 ID
     */
    public String getDeviceId() { return deviceId; }
    /**
     * 设置关联设备 ID。
     *
     * @param deviceId 关联设备 ID
     */
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getCommand() { return command; }
    /**
     * 设置字段含义待补充。
     *
     * @param command 字段含义待补充
     */
    public void setCommand(String command) { this.command = command; }

    /**
     * 获取年龄。
     *
     * @return 年龄
     */
    public String getSourceAgent() { return sourceAgent; }
    /**
     * 设置年龄。
     *
     * @param sourceAgent 年龄
     */
    public void setSourceAgent(String sourceAgent) { this.sourceAgent = sourceAgent; }

    /**
     * 获取结果。
     *
     * @return 结果
     */
    public String getResult() { return result; }
    /**
     * 设置结果。
     *
     * @param result 结果
     */
    public void setResult(String result) { this.result = result; }

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
}
