package com.anxinban.entity;


/**
 * AlarmProcess 实体类，对应数据库中的一张业务表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alarm_process")
public class AlarmProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "process_id", nullable = false, unique = true)
    /** 唯一标识，主键 */
    private String processId;

    @Column(name = "alarm_id", nullable = false)
    /** 唯一标识，主键 */
    private String alarmId;

    @Column(name = "handler_id")
    /** 唯一标识，主键 */
    private String handlerId;

    @Column(name = "handler_type")
    /** 类型标识 */
    private String handlerType;

    /** 字段含义待补充 */
    private String action;

    /** 结果 */
    private String result;

    /** 备注 */
    private String remark;

    @Column(name = "process_time")
    /** 字段含义待补充 */
    private LocalDateTime processTime;

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
    public String getProcessId() { return processId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param processId 唯一标识，主键
     */
    public void setProcessId(String processId) { this.processId = processId; }

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getAlarmId() { return alarmId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param alarmId 唯一标识，主键
     */
    public void setAlarmId(String alarmId) { this.alarmId = alarmId; }

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getHandlerId() { return handlerId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param handlerId 唯一标识，主键
     */
    public void setHandlerId(String handlerId) { this.handlerId = handlerId; }

    /**
     * 获取类型标识。
     *
     * @return 类型标识
     */
    public String getHandlerType() { return handlerType; }
    /**
     * 设置类型标识。
     *
     * @param handlerType 类型标识
     */
    public void setHandlerType(String handlerType) { this.handlerType = handlerType; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getAction() { return action; }
    /**
     * 设置字段含义待补充。
     *
     * @param action 字段含义待补充
     */
    public void setAction(String action) { this.action = action; }

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
     * 获取备注。
     *
     * @return 备注
     */
    public String getRemark() { return remark; }
    /**
     * 设置备注。
     *
     * @param remark 备注
     */
    public void setRemark(String remark) { this.remark = remark; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public LocalDateTime getProcessTime() { return processTime; }
    /**
     * 设置字段含义待补充。
     *
     * @param processTime 字段含义待补充
     */
    public void setProcessTime(LocalDateTime processTime) { this.processTime = processTime; }
}