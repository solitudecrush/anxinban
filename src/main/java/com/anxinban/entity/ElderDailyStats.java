package com.anxinban.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 老人每日健康统计 VIEW — 从 sensor_data 实时聚合，零冗余，永远同步。
 * 底层为 MySQL VIEW: CREATE VIEW elder_daily_stats AS SELECT ... FROM sensor_data GROUP BY elder_id, stat_date
 */
@Entity
@Immutable
@Table(name = "elder_daily_stats")
public class ElderDailyStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "elder_id", nullable = false, length = 50)
    private String elderId;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "avg_hr", precision = 5, scale = 2)
    private BigDecimal avgHr;

    @Column(name = "avg_spo2", precision = 5, scale = 2)
    private BigDecimal avgSpo2;

    @Column(name = "avg_temp", precision = 4, scale = 2)
    private BigDecimal avgTemp;

    @Column(name = "avg_systolic", precision = 5, scale = 1)
    private BigDecimal avgSystolic;

    @Column(name = "avg_diastolic", precision = 5, scale = 1)
    private BigDecimal avgDiastolic;

    @Column(name = "max_hr")
    private Integer maxHr;

    @Column(name = "min_spo2")
    private Integer minSpo2;

    @Column(name = "daily_tag", length = 50)
    private String dailyTag;

    @Column(name = "anxiety_score")
    private Integer anxietyScore;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getElderId() { return elderId; }
    public void setElderId(String elderId) { this.elderId = elderId; }
    public LocalDate getStatDate() { return statDate; }
    public void setStatDate(LocalDate statDate) { this.statDate = statDate; }
    public BigDecimal getAvgHr() { return avgHr; }
    public void setAvgHr(BigDecimal avgHr) { this.avgHr = avgHr; }
    public BigDecimal getAvgSpo2() { return avgSpo2; }
    public void setAvgSpo2(BigDecimal avgSpo2) { this.avgSpo2 = avgSpo2; }
    public BigDecimal getAvgTemp() { return avgTemp; }
    public void setAvgTemp(BigDecimal avgTemp) { this.avgTemp = avgTemp; }
    public BigDecimal getAvgSystolic() { return avgSystolic; }
    public void setAvgSystolic(BigDecimal avgSystolic) { this.avgSystolic = avgSystolic; }
    public BigDecimal getAvgDiastolic() { return avgDiastolic; }
    public void setAvgDiastolic(BigDecimal avgDiastolic) { this.avgDiastolic = avgDiastolic; }
    public Integer getMaxHr() { return maxHr; }
    public void setMaxHr(Integer maxHr) { this.maxHr = maxHr; }
    public Integer getMinSpo2() { return minSpo2; }
    public void setMinSpo2(Integer minSpo2) { this.minSpo2 = minSpo2; }
    public String getDailyTag() { return dailyTag; }
    public void setDailyTag(String dailyTag) { this.dailyTag = dailyTag; }
    public Integer getAnxietyScore() { return anxietyScore; }
    public void setAnxietyScore(Integer anxietyScore) { this.anxietyScore = anxietyScore; }
}
