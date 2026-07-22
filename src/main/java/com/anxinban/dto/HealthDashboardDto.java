package com.anxinban.dto;

import java.util.List;
import java.util.Map;

/**
 * 健康数据看板 DTO — 为前端"个人中心-健康数据看板"提供四项核心数据。
 *
 * <p>包含：睡眠分析、陪伴记录（情绪趋势）、音乐疗法记录、物品寻找记录。
 * 每个模块均包含后端生成的描述性总结语（summary），前端直接展示即可。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
public class HealthDashboardDto {

    /** 睡眠分析模块 */
    private SleepPanel sleepPanel;

    /** 陪伴记录模块（情感对话情绪趋势） */
    private CompanionPanel companionPanel;

    /** 音乐疗法模块 */
    private MusicPanel musicPanel;

    /** 物品寻找模块 */
    private ItemFindingPanel itemFindingPanel;

    // ==================== Getters and Setters ====================

    public SleepPanel getSleepPanel() { return sleepPanel; }
    public void setSleepPanel(SleepPanel sleepPanel) { this.sleepPanel = sleepPanel; }

    public CompanionPanel getCompanionPanel() { return companionPanel; }
    public void setCompanionPanel(CompanionPanel companionPanel) { this.companionPanel = companionPanel; }

    public MusicPanel getMusicPanel() { return musicPanel; }
    public void setMusicPanel(MusicPanel musicPanel) { this.musicPanel = musicPanel; }

    public ItemFindingPanel getItemFindingPanel() { return itemFindingPanel; }
    public void setItemFindingPanel(ItemFindingPanel itemFindingPanel) { this.itemFindingPanel = itemFindingPanel; }

    // ==================== Inner Classes ====================

    /**
     * 睡眠分析面板数据
     */
    public static class SleepPanel {
        /** 描述性总结语，如"昨晚睡得不错，时长充足" */
        private String summary;
        /** 近7天睡眠记录列表（按日期升序） */
        private List<SleepDayItem> records;

        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public List<SleepDayItem> getRecords() { return records; }
        public void setRecords(List<SleepDayItem> records) { this.records = records; }
    }

    /**
     * 单日睡眠数据项
     */
    public static class SleepDayItem {
        /** 日期，格式 yyyy-MM-dd */
        private String date;
        /** 总睡眠时长（小时） */
        private Double totalSleepHours;
        /** 深睡占比 % */
        private Integer deepSleepPercent;
        /** 夜间醒来次数 */
        private Integer wakeCount;

        public SleepDayItem() {}
        public SleepDayItem(String date, Double totalSleepHours, Integer deepSleepPercent, Integer wakeCount) {
            this.date = date;
            this.totalSleepHours = totalSleepHours;
            this.deepSleepPercent = deepSleepPercent;
            this.wakeCount = wakeCount;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public Double getTotalSleepHours() { return totalSleepHours; }
        public void setTotalSleepHours(Double totalSleepHours) { this.totalSleepHours = totalSleepHours; }
        public Integer getDeepSleepPercent() { return deepSleepPercent; }
        public void setDeepSleepPercent(Integer deepSleepPercent) { this.deepSleepPercent = deepSleepPercent; }
        public Integer getWakeCount() { return wakeCount; }
        public void setWakeCount(Integer wakeCount) { this.wakeCount = wakeCount; }
    }

    /**
     * 陪伴记录面板数据（情感对话情绪趋势）
     */
    public static class CompanionPanel {
        /** 描述性总结语，如"最近情绪稳定，状态良好" */
        private String summary;
        /** 近30天情绪标签分布统计 {"开心": 5, "平静": 3, "低落": 1, ...} */
        private Map<String, Long> emotionStats;
        /** 最近5条陪伴记录 */
        private List<CompanionItem> recentRecords;

        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public Map<String, Long> getEmotionStats() { return emotionStats; }
        public void setEmotionStats(Map<String, Long> emotionStats) { this.emotionStats = emotionStats; }
        public List<CompanionItem> getRecentRecords() { return recentRecords; }
        public void setRecentRecords(List<CompanionItem> recentRecords) { this.recentRecords = recentRecords; }
    }

    /**
     * 单条陪伴记录项
     */
    public static class CompanionItem {
        /** 交互时间，格式 yyyy-MM-dd HH:mm */
        private String time;
        /** 对话摘要 */
        private String summary;
        /** 情绪标签 */
        private String emotion;
        /** 情绪标签颜色 */
        private String emotionColor;

        public CompanionItem() {}
        public CompanionItem(String time, String summary, String emotion, String emotionColor) {
            this.time = time;
            this.summary = summary;
            this.emotion = emotion;
            this.emotionColor = emotionColor;
        }

        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getEmotion() { return emotion; }
        public void setEmotion(String emotion) { this.emotion = emotion; }
        public String getEmotionColor() { return emotionColor; }
        public void setEmotionColor(String emotionColor) { this.emotionColor = emotionColor; }
    }

    /**
     * 音乐疗法面板数据
     */
    public static class MusicPanel {
        /** 描述性总结语，如"近期有听音乐放松，继续保持" */
        private String summary;
        /** 近7天音乐播放记录 */
        private List<MusicItem> recentRecords;

        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public List<MusicItem> getRecentRecords() { return recentRecords; }
        public void setRecentRecords(List<MusicItem> recentRecords) { this.recentRecords = recentRecords; }
    }

    /**
     * 单条音乐疗法记录项
     */
    public static class MusicItem {
        /** 播放时间 */
        private String time;
        /** 音乐类型 */
        private String musicType;
        /** 老人指令/请求原文 */
        private String userText;

        public MusicItem() {}
        public MusicItem(String time, String musicType, String userText) {
            this.time = time;
            this.musicType = musicType;
            this.userText = userText;
        }

        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getMusicType() { return musicType; }
        public void setMusicType(String musicType) { this.musicType = musicType; }
        public String getUserText() { return userText; }
        public void setUserText(String userText) { this.userText = userText; }
    }

    /**
     * 物品寻找面板数据
     */
    public static class ItemFindingPanel {
        /** 最近5次寻找记录 */
        private List<ItemFindingItem> recentRecords;

        public List<ItemFindingItem> getRecentRecords() { return recentRecords; }
        public void setRecentRecords(List<ItemFindingItem> recentRecords) { this.recentRecords = recentRecords; }
    }

    /**
     * 单条物品寻找记录项
     */
    public static class ItemFindingItem {
        /** 寻找时间 */
        private String time;
        /** 寻找的物品 */
        private String item;
        /** 物品位置 */
        private String location;
        /** 查找结果 */
        private String result;

        public ItemFindingItem() {}
        public ItemFindingItem(String time, String item, String location, String result) {
            this.time = time;
            this.item = item;
            this.location = location;
            this.result = result;
        }

        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getItem() { return item; }
        public void setItem(String item) { this.item = item; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
    }
}
