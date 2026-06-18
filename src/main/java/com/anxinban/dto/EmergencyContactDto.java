package com.anxinban.dto;


/**
 * EmergencyContact 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

public class EmergencyContactDto {
    /** 唯一标识，主键 */
    private String contactId;
    /** 关联老人用户 ID */
    private String elderId;
    /** 名称 */
    private String name;
    /** 手机号 */
    private String phone;
    /** 关系描述 */
    private String relation;
    /** 字段含义待补充 */
    private Boolean isPrimary;
    /** 字段含义待补充 */
    private Integer sortOrder;

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getContactId() {
        return contactId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param contactId 唯一标识，主键
     */
    public void setContactId(String contactId) {
        this.contactId = contactId;
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
     * 获取关系描述。
     *
     * @return 关系描述
     */
    public String getRelation() {
        return relation;
    }

    /**
     * 设置关系描述。
     *
     * @param relation 关系描述
     */
    public void setRelation(String relation) {
        this.relation = relation;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public Boolean getIsPrimary() {
        return isPrimary;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param isPrimary 字段含义待补充
     */
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param sortOrder 字段含义待补充
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
