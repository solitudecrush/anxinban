package com.anxinban.dto;


/**
 * Notification 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

public class NotificationDto {
    /** 唯一标识，主键 */
    private String notificationId;
    /** 关联用户 ID */
    private String userId;
    /** 类型标识 */
    private String userType;
    /** 类型标识 */
    private String notificationType;
    /** 标题 */
    private String title;
    /** 内容 */
    private String content;
    /** 是否已读 */
    private Boolean isRead;
    /** 字段含义待补充 */
    private String building;
    /** 房间名称/编号 */
    private String room;
    /** 唯一标识，主键 */
    private String orderId;
    /** 唯一标识，主键 */
    private String requestId;
    /** 关联老人用户 ID */
    private String elderId;
    /** 唯一标识，主键 */
    private String relatedId;
    /** 记录创建时间 */
    private String createTime;

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getNotificationId() {
        return notificationId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param notificationId 唯一标识，主键
     */
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
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
     * 获取类型标识。
     *
     * @return 类型标识
     */
    public String getUserType() {
        return userType;
    }

    /**
     * 设置类型标识。
     *
     * @param userType 类型标识
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * 获取类型标识。
     *
     * @return 类型标识
     */
    public String getNotificationType() {
        return notificationType;
    }

    /**
     * 设置类型标识。
     *
     * @param notificationType 类型标识
     */
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    /**
     * 获取标题。
     *
     * @return 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题。
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取内容。
     *
     * @return 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容。
     *
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取是否已读。
     *
     * @return 是否已读
     */
    public Boolean getIsRead() {
        return isRead;
    }

    /**
     * 设置是否已读。
     *
     * @param isRead 是否已读
     */
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
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
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param orderId 唯一标识，主键
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param requestId 唯一标识，主键
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
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
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getRelatedId() {
        return relatedId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param relatedId 唯一标识，主键
     */
    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    /**
     * 获取记录创建时间。
     *
     * @return 记录创建时间
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * 设置记录创建时间。
     *
     * @param createTime 记录创建时间
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
