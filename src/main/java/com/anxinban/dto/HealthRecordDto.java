package com.anxinban.dto;


/**
 * HealthRecord 数据传输对象（DTO），用于层与层之间的数据传递。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

public class HealthRecordDto {
    /** 唯一标识，主键 */
    private String recordId;
    /** 关联老人用户 ID */
    private String elderId;
    /** 字段含义待补充 */
    private String hospitalizationInfo;
    /** 字段含义待补充 */
    private String medicalHistory;
    /** 字段含义待补充 */
    private String allergyHistory;
    /** 字段含义待补充 */
    private String commonMedications;
    /** 类型标识 */
    private String bloodType;
    /** 备注 */
    private String remarks;

    /**
     * 获取唯一标识，主键。
     *
     * @return 唯一标识，主键
     */
    public String getRecordId() {
        return recordId;
    }

    /**
     * 设置唯一标识，主键。
     *
     * @param recordId 唯一标识，主键
     */
    public void setRecordId(String recordId) {
        this.recordId = recordId;
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
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getHospitalizationInfo() {
        return hospitalizationInfo;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param hospitalizationInfo 字段含义待补充
     */
    public void setHospitalizationInfo(String hospitalizationInfo) {
        this.hospitalizationInfo = hospitalizationInfo;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getMedicalHistory() {
        return medicalHistory;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param medicalHistory 字段含义待补充
     */
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getAllergyHistory() {
        return allergyHistory;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param allergyHistory 字段含义待补充
     */
    public void setAllergyHistory(String allergyHistory) {
        this.allergyHistory = allergyHistory;
    }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getCommonMedications() {
        return commonMedications;
    }

    /**
     * 设置字段含义待补充。
     *
     * @param commonMedications 字段含义待补充
     */
    public void setCommonMedications(String commonMedications) {
        this.commonMedications = commonMedications;
    }

    /**
     * 获取类型标识。
     *
     * @return 类型标识
     */
    public String getBloodType() {
        return bloodType;
    }

    /**
     * 设置类型标识。
     *
     * @param bloodType 类型标识
     */
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    /**
     * 获取备注。
     *
     * @return 备注
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置备注。
     *
     * @param remarks 备注
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
