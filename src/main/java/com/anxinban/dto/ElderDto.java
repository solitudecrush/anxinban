package com.anxinban.dto;


/**
 * Elder 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import java.util.List;

public class ElderDto {
    /** 关联老人用户 ID */
    private String elderId;
    /** 名称 */
    private String name;
    /** 性别 */
    private String gender;
    /** 年龄 */
    private Integer age;
    /** 手机号 */
    private String phone;
    /** 手机号 */
    private String guardianPhone;
    /** 状态标识 */
    private String healthStatus;
    /** 状态标识 */
    private String healthStatusText;
    /** 地址 */
    private String address;
    /** 字段含义待补充 */
    private String building;
    /** 房间名称/编号 */
    private String room;
    /** 字段含义待补充 */
    private String healthNote;
    /** 手机号 */
    private String familyPhone;
    /** 头像 URL */
    private String avatar;
    /** 字段含义待补充 */
    private Boolean hasCamera;
    /** 字段含义待补充 */
    private Long cameraAuthUntil;
    /** 字段含义待补充 */
    private Boolean cameraPending;
    /** 是否在线 */
    private String lastOnline;
    /** 字段含义待补充 */
    private List<String> tags;

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
    public String getName() {
        return name;
    }

    /**
     * 设置名称。
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取性别。
     *
     * @return 性别
     */
    public String getGender() {
        return gender;
    }

    /**
     * 设置性别。
     *
     * @param gender 性别
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * 获取年龄。
     *
     * @return 年龄
     */
    public Integer getAge() {
        return age;
    }

    /**
     * 设置年龄。
     *
     * @param age 年龄
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * 获取手机号。
     *
     * @return 手机号
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置手机号。
     *
     * @param phone 手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取手机号。
     *
     * @return 手机号
     */
    public String getGuardianPhone() {
        return guardianPhone;
    }

    /**
     * 设置手机号。
     *
     * @param guardianPhone 手机号
     */
    public void setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
    }

    /**
     * 获取状态标识。
     *
     * @return 状态标识
     */
    public String getHealthStatus() {
        return healthStatus;
    }

    /**
     * 设置状态标识。
     *
     * @param healthStatus 状态标识
     */
    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    /**
     * 获取状态标识。
     *
     * @return 状态标识
     */
    public String getHealthStatusText() {
        return healthStatusText;
    }

    /**
     * 设置状态标识。
     *
     * @param healthStatusText 状态标识
     */
    public void setHealthStatusText(String healthStatusText) {
        this.healthStatusText = healthStatusText;
    }

    /**
     * 获取地址。
     *
     * @return 地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置地址。
     *
     * @param address 地址
     */
    public void setAddress(String address) {
        this.address = address;
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
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getHealthNote() {
        return healthNote;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param healthNote 字段含义待补充
     */
    public void setHealthNote(String healthNote) {
        this.healthNote = healthNote;
    }

    /**
     * 获取手机号。
     *
     * @return 手机号
     */
    public String getFamilyPhone() {
        return familyPhone;
    }

    /**
     * 设置手机号。
     *
     * @param familyPhone 手机号
     */
    public void setFamilyPhone(String familyPhone) {
        this.familyPhone = familyPhone;
    }

    /**
     * 获取头像 URL。
     *
     * @return 头像 URL
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置头像 URL。
     *
     * @param avatar 头像 URL
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public Boolean getHasCamera() {
        return hasCamera;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param hasCamera 字段含义待补充
     */
    public void setHasCamera(Boolean hasCamera) {
        this.hasCamera = hasCamera;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public Long getCameraAuthUntil() {
        return cameraAuthUntil;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param cameraAuthUntil 字段含义待补充
     */
    public void setCameraAuthUntil(Long cameraAuthUntil) {
        this.cameraAuthUntil = cameraAuthUntil;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public Boolean getCameraPending() {
        return cameraPending;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param cameraPending 字段含义待补充
     */
    public void setCameraPending(Boolean cameraPending) {
        this.cameraPending = cameraPending;
    }

    /**
     * 获取是否在线。
     *
     * @return 是否在线
     */
    public String getLastOnline() {
        return lastOnline;
    }

    /**
     * 设置是否在线。
     *
     * @param lastOnline 是否在线
     */
    public void setLastOnline(String lastOnline) {
        this.lastOnline = lastOnline;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param tags 字段含义待补充
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
