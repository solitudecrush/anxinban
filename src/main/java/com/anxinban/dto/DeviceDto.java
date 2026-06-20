package com.anxinban.dto;


/**
 * Device 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import java.util.List;
import java.util.Map;

/**
 * 设备信息数据传输对象。
 *
 * <p>用于封装养老场景中各类智能设备（如健康监测、跌倒检测、门磁、摄像头等）的
 * 基本信息、运行状态、位置信息、传感器数据及下发的控制指令。</p>
 */
public class DeviceDto {

    /** 设备唯一标识，必填，示例：DEV202406150001 */
    private String deviceId;

    /** 设备关联的老人唯一标识，示例：ELD202406150001 */
    private String elderId;

    /** 设备关联的老人姓名，示例：张爷爷 */
    private String elderName;

    /** 设备类型，如 BLOOD_PRESSURE、FALL_DETECTOR、CAMERA 等，必填，示例：BLOOD_PRESSURE */
    private String type;

    /** 设备名称，示例：客厅血压计 */
    private String name;

    /** 设备在线状态，如 ONLINE、OFFLINE、FAULT，必填，示例：ONLINE */
    private String status;

    /** 设备最近一次上报/心跳时间，格式 yyyy-MM-dd HH:mm:ss，示例：2024-06-15 14:30:00 */
    private String lastHeartbeat;

    /** 设备安装位置描述，示例：客厅沙发旁 */
    private String location;

    /** 设备所在楼栋，示例：A 栋 */
    private String building;

    /** 设备所在房间，示例：1201 */
    private String room;

    /** 设备电池电量百分比，取值范围 0-100，示例：85 */
    private Integer battery;

    /** 当前已连接的子设备数量，适用于网关类设备，示例：3 */
    private Integer connectedDevices;

    /** 设备传感器最近一次采集的数据列表，示例：温度、湿度、血压读数等 */
    private List<SensorData> sensorDataList;

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
     * 获取关联老人用户 ID。
     *
     * @return 关联老人用户 ID
     */
    public String getElderId() {
        return elderId;
    }

    /**
     * 设置关联老人用户 ID。
     *
     * @param elderId 关联老人用户 ID
     */
    public void setElderId(String elderId) {
        this.elderId = elderId;
    }

    /**
     * 获取名称。
     *
     * @return 名称
     */
    public String getElderName() {
        return elderName;
    }

    /**
     * 设置名称。
     *
     * @param elderName 名称
     */
    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    /**
     * 获取设备类型。
     *
     * @return 设备类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置设备类型。
     *
     * @param type 设备类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取设备名称。
     *
     * @return 设备名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置设备名称。
     *
     * @param name 设备名称
     */
    public void setName(String name) {
        this.name = name;
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
     * 获取位置信息。
     *
     * @return 位置信息
     */
    public String getLocation() {
        return location;
    }

    /**
     * 设置位置信息。
     *
     * @param location 位置信息
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getBuilding() {
        return building;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param building 字段含义待补充
     */
    public void setBuilding(String building) {
        this.building = building;
    }

    /**
     * 获取房间名称/编号。
     *
     * @return 房间名称/编号
     */
    public String getRoom() {
        return room;
    }

    /**
     * 设置房间名称/编号。
     *
     * @param room 房间名称/编号
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * 获取级别。
     *
     * @return 级别
     */
    public Integer getBattery() {
        return battery;
    }

    /**
     * 设置级别。
     *
     * @param battery 级别
     */
    public void setBattery(Integer battery) {
        this.battery = battery;
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
     * 获取数据载荷。
     *
     * @return 数据载荷
     */
    public List<SensorData> getSensorDataList() {
        return sensorDataList;
    }

    /**
     * 设置数据载荷。
     *
     * @param sensorDataList 数据载荷
     */
    public void setSensorDataList(List<SensorData> sensorDataList) {
        this.sensorDataList = sensorDataList;
    }

    /**
     * 设备传感器采集数据项。
     *
     * <p>描述某一时刻传感器的读数，包括传感器类型、数值、单位、采集时间等。</p>
     */
    public static class SensorData {

        /** 传感器唯一标识，必填，示例：SEN202406150001 */
        private String sensorId;

        /** 传感器类型，如 TEMPERATURE、HEART_RATE、BLOOD_PRESSURE 等，必填，示例：TEMPERATURE */
        private String sensorType;

        /** 传感器读数值，类型根据传感器不同可能是数值、字符串或对象，示例：36.5 */
        private Object value;

        /** 数值单位，如 ℃、bpm、mmHg，示例：℃ */
        private String unit;

        /** 数据采集时间，格式 yyyy-MM-dd HH:mm:ss，必填，示例：2024-06-15 14:30:00 */
        private String timestamp;

        /** 数据状态，如 NORMAL、ABNORMAL、OFFLINE，示例：NORMAL */
        private String status;

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
        public String getSensorId() {
            return sensorId;
        }

    /**
     * 设置唯一标识，主键。
     *
     * @param sensorId 唯一标识，主键
     */
        public void setSensorId(String sensorId) {
            this.sensorId = sensorId;
        }

    /**
     * 获取类型标识。
     *
     * @return 类型标识
     */
        public String getSensorType() {
            return sensorType;
        }

    /**
     * 设置类型标识。
     *
     * @param sensorType 类型标识
     */
        public void setSensorType(String sensorType) {
            this.sensorType = sensorType;
        }

    /**
     * 获取数值。
     *
     * @return 数值
     */
        public Object getValue() {
            return value;
        }

    /**
     * 设置数值。
     *
     * @param value 数值
     */
        public void setValue(Object value) {
            this.value = value;
        }

    /**
     * 获取单位。
     *
     * @return 单位
     */
        public String getUnit() {
            return unit;
        }

    /**
     * 设置单位。
     *
     * @param unit 单位
     */
        public void setUnit(String unit) {
            this.unit = unit;
        }

    /**
     * 获取时间戳。
     *
     * @return 时间戳
     */
        public String getTimestamp() {
            return timestamp;
        }

    /**
     * 设置时间戳。
     *
     * @param timestamp 时间戳
     */
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
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
    }

    /**
     * 设备控制指令信息。
     *
     * <p>描述向设备下发的控制命令，包括命令类型、参数、执行状态及下发时间。</p>
     */
    public static class Command {

        /** 指令唯一标识，必填，示例：CMD202406150001 */
        private String commandId;

        /** 指令目标设备唯一标识，必填，示例：DEV202406150001 */
        private String deviceId;

        /** 指令类型，如 REBOOT、CALL、SET_VOLUME 等，必填，示例：REBOOT */
        private String commandType;

        /** 指令参数集合，键值对形式，示例：{"volume": 80, "duration": 30} */
        private Map<String, Object> parameters;

        /** 指令执行状态，如 PENDING、SENT、SUCCESS、FAILED，必填，示例：SUCCESS */
        private String status;

        /** 指令下发或更新时间，格式 yyyy-MM-dd HH:mm:ss，必填，示例：2024-06-15 14:30:00 */
        private String timestamp;

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
        public String getCommandId() {
            return commandId;
        }

    /**
     * 设置唯一标识，主键。
     *
     * @param commandId 唯一标识，主键
     */
        public void setCommandId(String commandId) {
            this.commandId = commandId;
        }

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
     * 获取类型标识。
     *
     * @return 类型标识
     */
        public String getCommandType() {
            return commandType;
        }

    /**
     * 设置类型标识。
     *
     * @param commandType 类型标识
     */
        public void setCommandType(String commandType) {
            this.commandType = commandType;
        }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
        public Map<String, Object> getParameters() {
            return parameters;
        }

    /**
     * 设置字段含义待补充。
     *
     * @param parameters 字段含义待补充
     */
        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
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
     * 获取时间戳。
     *
     * @return 时间戳
     */
        public String getTimestamp() {
            return timestamp;
        }

    /**
     * 设置时间戳。
     *
     * @param timestamp 时间戳
     */
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
