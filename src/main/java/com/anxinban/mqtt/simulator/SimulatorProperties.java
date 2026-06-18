package com.anxinban.mqtt.simulator;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 设备模拟器配置类。
 *
 * 作用：控制硬件模拟器的启动开关、默认房屋/房间、数据发送间隔等参数。
 * 在 application.properties 中通过 mqtt.simulator.* 前缀配置。
 */
@Component
@ConfigurationProperties(prefix = "mqtt.simulator")
public class SimulatorProperties {
    private boolean enabled = false;
    private String houseId = "demo-house";
    private String room = "living-room";
    private int intervalSeconds = 30;
    private String clientId = "device-simulator";

    // Getters and Setters

        /**
         * 判断是否是否启用。
         *
         * @return 是否是否启用
         */
    public boolean isEnabled() {
        return enabled;
    }

        /**
         * 设置是否启用。
         *
         * @param enabled 是否启用
         */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

        /**
         * 获取房屋/家庭 ID。
         *
         * @return 房屋/家庭 ID
         */
    public String getHouseId() {
        return houseId;
    }

        /**
         * 设置房屋/家庭 ID。
         *
         * @param houseId 房屋/家庭 ID
         */
    public void setHouseId(String houseId) {
        this.houseId = houseId;
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
         * 获取间隔。
         *
         * @return 间隔
         */
    public int getIntervalSeconds() {
        return intervalSeconds;
    }

        /**
         * 设置间隔。
         *
         * @param intervalSeconds 间隔
         */
    public void setIntervalSeconds(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

        /**
         * 获取客户端 ID。
         *
         * @return 客户端 ID
         */
    public String getClientId() {
        return clientId;
    }

        /**
         * 设置客户端 ID。
         *
         * @param clientId 客户端 ID
         */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
