package com.anxinban.dto;


/**
 * AgentInfo 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import java.util.List;

/**
 * 智能代理（Agent）信息数据传输对象。
 *
 * <p>用于封装边缘网关、采集代理等设备的运行状态、接入设备数量以及故障信息。
 * 通常在设备管理、代理监控接口中作为响应数据返回。</p>
 */
public class AgentInfoDto {

    /** 代理唯一标识，必填，示例：AGT202406150001 */
    private String agentId;

    /** 代理类型，如 GATEWAY、COLLECTOR 等，必填，示例：GATEWAY */
    private String agentType;

    /** 代理运行状态，如 ONLINE、OFFLINE、FAULT，必填，示例：ONLINE */
    private String status;

    /** 最近一次心跳时间，格式 yyyy-MM-dd HH:mm:ss，示例：2024-06-15 14:30:00 */
    private String lastHeartbeat;

    /** 代理所在 IP 地址，示例：192.168.1.100 */
    private String ip;

    /** 该代理下挂接的设备总数，示例：12 */
    private Integer deviceCount;

    /** 当前已连接的设备数量，示例：10 */
    private Integer connectedDevices;

    /** 代理下各设备的故障列表，无故障时可为空列表或 null */
    private List<Fault> faults;

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getAgentId() {
        return agentId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param agentId 唯一标识，主键
     */
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    /**
     * 获取类型标识。
     *
     * @return 类型标识
     */
    public String getAgentType() {
        return agentType;
    }

    /**
     * 设置类型标识。
     *
     * @param agentType 类型标识
     */
    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    /**
     * 获取状态标识。
     *
     * @return 状态标识
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态标识。
     *
     * @param status 状态标识
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getLastHeartbeat() {
        return lastHeartbeat;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param lastHeartbeat 字段含义待补充
     */
    public void setLastHeartbeat(String lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param ip 字段含义待补充
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public Integer getDeviceCount() {
        return deviceCount;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param deviceCount 字段含义待补充
     */
    public void setDeviceCount(Integer deviceCount) {
        this.deviceCount = deviceCount;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public Integer getConnectedDevices() {
        return connectedDevices;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param connectedDevices 字段含义待补充
     */
    public void setConnectedDevices(Integer connectedDevices) {
        this.connectedDevices = connectedDevices;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public List<Fault> getFaults() {
        return faults;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param faults 字段含义待补充
     */
    public void setFaults(List<Fault> faults) {
        this.faults = faults;
    }

    /**
     * 代理设备故障明细。
     *
     * <p>描述代理下属设备出现的故障编码与故障描述。</p>
     */
    public static class Fault {

        /** 故障设备唯一标识，必填，示例：DEV202406150001 */
        private String deviceId;

        /** 故障编码，必填，示例：SENSOR_OFFLINE */
        private String faultCode;

        /** 故障描述信息，必填，示例：温湿度传感器已离线超过 10 分钟 */
        private String message;

    /**
     * 获取关联设备 ID。
     *
     * @return 关联设备 ID
     */
        public String getDeviceId() {
            return deviceId;
        }

    /**
     * 设置关联设备 ID。
     *
     * @param deviceId 关联设备 ID
     */
        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

    /**
     * 获取状态码。
     *
     * @return 状态码
     */
        public String getFaultCode() {
            return faultCode;
        }

    /**
     * 设置状态码。
     *
     * @param faultCode 状态码
     */
        public void setFaultCode(String faultCode) {
            this.faultCode = faultCode;
        }

    /**
     * 获取提示信息。
     *
     * @return 提示信息
     */
        public String getMessage() {
            return message;
        }

    /**
     * 设置提示信息。
     *
     * @param message 提示信息
     */
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
