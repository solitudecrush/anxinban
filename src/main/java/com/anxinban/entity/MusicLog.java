package com.anxinban.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 音乐播放日志实体 — 存储每日音乐播放记录，用于健康详情页。
 *
 * <p>对应数据表：music_logs</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Entity
@Table(name = "music_logs")
public class MusicLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联用户 ID */
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    /** 播放日期 */
    @Column(nullable = false)
    private LocalDate date;

    /** 歌曲/音频名称 */
    @Column(name = "song_name", nullable = false, length = 200)
    private String songName;

    /** 播放时长（分钟） */
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    /** 场景标签（如 助眠 / 晨醒 / 放松） */
    @Column(length = 50)
    private String scene;

    /** 记录创建时间 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ==================== Getters and Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getSongName() { return songName; }
    public void setSongName(String songName) { this.songName = songName; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getScene() { return scene; }
    public void setScene(String scene) { this.scene = scene; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
