package com.anxinban.entity;


/**
 * HealthRecord 实体类，对应数据库中的一张业务表。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_record")
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 唯一标识，主键 */
    private Long id;

    @Column(name = "record_id", nullable = false, unique = true)
    /** 唯一标识，主键 */
    private String recordId;

    @Column(name = "elder_id", nullable = false)
    /** 关联老人用户 ID */
    private String elderId;

    @Column(name = "hospitalization_info")
    /** 字段含义待补充 */
    private String hospitalizationInfo;

    @Column(name = "medical_history")
    /** 字段含义待补充 */
    private String medicalHistory;

    @Column(name = "allergy_history")
    /** 字段含义待补充 */
    private String allergyHistory;

    @Column(name = "common_medications")
    /** 字段含义待补充 */
    private String commonMedications;

    @Column(name = "blood_type")
    /** 类型标识 */
    private String bloodType;

    /** 备注 */
    private String remarks;

    @Column(name = "created_at")
    /** 记录创建时间 */
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    /** 记录最后更新时间 */
    private LocalDateTime updatedAt;

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
    public String getRecordId() { return recordId; }
    /**
     * 设置唯一标识，主键。
     *
     * @param recordId 唯一标识，主键
     */
    public void setRecordId(String recordId) { this.recordId = recordId; }

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
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getHospitalizationInfo() { return hospitalizationInfo; }
    /**
     * 设置字段含义待补充。
     *
     * @param hospitalizationInfo 字段含义待补充
     */
    public void setHospitalizationInfo(String hospitalizationInfo) { this.hospitalizationInfo = hospitalizationInfo; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getMedicalHistory() { return medicalHistory; }
    /**
     * 设置字段含义待补充。
     *
     * @param medicalHistory 字段含义待补充
     */
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getAllergyHistory() { return allergyHistory; }
    /**
     * 设置字段含义待补充。
     *
     * @param allergyHistory 字段含义待补充
     */
    public void setAllergyHistory(String allergyHistory) { this.allergyHistory = allergyHistory; }

    /**
     * 获取字段含义待补充。
     *
     * @return 字段含义待补充
     */
    public String getCommonMedications() { return commonMedications; }
    /**
     * 设置字段含义待补充。
     *
     * @param commonMedications 字段含义待补充
     */
    public void setCommonMedications(String commonMedications) { this.commonMedications = commonMedications; }

    /**
     * 获取类型标识。
     *
     * @return 类型标识
     */
    public String getBloodType() { return bloodType; }
    /**
     * 设置类型标识。
     *
     * @param bloodType 类型标识
     */
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    /**
     * 获取备注。
     *
     * @return 备注
     */
    public String getRemarks() { return remarks; }
    /**
     * 设置备注。
     *
     * @param remarks 备注
     */
    public void setRemarks(String remarks) { this.remarks = remarks; }

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

    /**
     * 获取记录最后更新时间。
     *
     * @return 记录最后更新时间
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    /**
     * 设置记录最后更新时间。
     *
     * @param updatedAt 记录最后更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
