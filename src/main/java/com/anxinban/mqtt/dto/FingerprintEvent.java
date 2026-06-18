package com.anxinban.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 指纹识别事件消息。
 *
 * 对应硬件：大门或房间门锁上的指纹识别模块。
 * 当用户按压指纹时触发，上报识别结果；若失败可联动门口摄像头抓拍。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FingerprintEvent extends BaseDeviceMessage {
    private String result;
    private String userId;
    private String userName;
    private String location;

    /** 失败时的重试次数 */
    private Integer retryCount;

    public FingerprintEvent() {
        super();
    }

    public FingerprintEvent(String deviceId, String result, String location) {
        super(deviceId);
        this.result = result;
        this.location = location;
    }

    // Getters and Setters

        /**
         * 获取结果。
         *
         * @return 结果
         */
    public String getResult() {
        return result;
    }

        /**
         * 设置结果。
         *
         * @param result 结果
         */
    public void setResult(String result) {
        this.result = result;
    }

        /**
         * 获取关联用户 ID。
         *
         * @return 关联用户 ID
         */
    public String getUserId() {
        return userId;
    }

        /**
         * 设置关联用户 ID。
         *
         * @param userId 关联用户 ID
         */
    public void setUserId(String userId) {
        this.userId = userId;
    }

        /**
         * 获取用户名。
         *
         * @return 用户名
         */
    public String getUserName() {
        return userName;
    }

        /**
         * 设置用户名。
         *
         * @param userName 用户名
         */
    public void setUserName(String userName) {
        this.userName = userName;
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
         * 获取数量。
         *
         * @return 数量
         */
    public Integer getRetryCount() {
        return retryCount;
    }

        /**
         * 设置数量。
         *
         * @param retryCount 数量
         */
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
}
