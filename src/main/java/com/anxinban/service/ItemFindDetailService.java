package com.anxinban.service;

import com.anxinban.entity.AiServiceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 物品寻找详情服务。
 *
 * <p>数据来源：ai_service_record 表（service_type=find_item）。
 * 字段映射：item → itemName, location → position, result='found' → found=true,
 * durationSeconds 由 interaction_time - created_at 计算得出。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class ItemFindDetailService {

    private static final Logger log = LoggerFactory.getLogger(ItemFindDetailService.class);
    private static final int DEFAULT_DURATION_SECONDS = 15;

    private final AiServiceRecordService aiServiceRecordService;

    @Autowired
    public ItemFindDetailService(AiServiceRecordService aiServiceRecordService) {
        this.aiServiceRecordService = aiServiceRecordService;
    }

    /**
     * 查询时间范围内的物品寻找记录（来自 ai_service_record）：
     * - list: 按日期升序排列的历史记录
     * - totalCount: 总记录数
     * - successRate: 成功率（找到的次数/总次数）
     *
     * @param elderId   老人 ID
     * @param startDate 开始日期（含）
     * @param endDate   结束日期（含）
     */
    public Map<String, Object> getItemFindDetail(String elderId, LocalDate startDate, LocalDate endDate) {
        List<AiServiceRecord> allRecords = aiServiceRecordService.listByElderAndType(elderId, "find_item");

        LocalDateTime startDt = startDate.atStartOfDay();
        LocalDateTime endDt = endDate.plusDays(1).atStartOfDay();

        // 过滤时间范围内的记录
        List<AiServiceRecord> records = allRecords.stream()
                .filter(r -> r.getInteractionTime() != null
                        && !r.getInteractionTime().isBefore(startDt)
                        && r.getInteractionTime().isBefore(endDt))
                .sorted(Comparator.comparing(AiServiceRecord::getInteractionTime))
                .collect(Collectors.toList());

        int totalCount = records.size();
        int foundCount = 0;

        List<Map<String, Object>> list = new ArrayList<>();
        for (AiServiceRecord r : records) {
            Map<String, Object> item = new LinkedHashMap<>();

            // 日期：取 interaction_time 的 DATE 部分
            item.put("date", r.getInteractionTime().toLocalDate().toString());

            // item → itemName
            item.put("itemName", r.getItem() != null ? r.getItem() : "未知");

            // durationSeconds：由 interaction_time - created_at 计算
            int durationSeconds = DEFAULT_DURATION_SECONDS;
            if (r.getCreatedAt() != null && r.getInteractionTime() != null) {
                long seconds = ChronoUnit.SECONDS.between(r.getCreatedAt(), r.getInteractionTime());
                if (seconds > 0 && seconds < 3600) {
                    durationSeconds = (int) seconds;
                }
            }
            item.put("durationSeconds", durationSeconds);

            // result='found' → found=true
            boolean found = "found".equalsIgnoreCase(r.getResult());
            item.put("found", found);

            // location → position
            item.put("position", r.getLocation() != null ? r.getLocation() : "未知");

            list.add(item);

            if (found) {
                foundCount++;
            }
        }

        double successRate = totalCount > 0 ? round2((double) foundCount / totalCount) : 0.0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("list", list);
        result.put("totalCount", totalCount);
        result.put("successRate", successRate);
        return result;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
