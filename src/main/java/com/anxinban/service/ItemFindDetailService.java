package com.anxinban.service;

import com.anxinban.entity.ItemFindLog;
import com.anxinban.mapper.ItemFindLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * 物品寻找详情服务。
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class ItemFindDetailService {

    private static final Logger log = LoggerFactory.getLogger(ItemFindDetailService.class);
    private final ItemFindLogRepository repository;

    @Autowired
    public ItemFindDetailService(ItemFindLogRepository repository) {
        this.repository = repository;
    }

    /**
     * 查询时间范围内的物品寻找记录：
     * - list: 按日期升序排列的历史记录
     * - totalCount: 总记录数
     * - successRate: 成功率（找到的次数/总次数）
     */
    public Map<String, Object> getItemFindDetail(String userId, LocalDate startDate, LocalDate endDate) {
        List<ItemFindLog> records = repository.findByUserIdAndDateBetweenOrderByDateAsc(userId, startDate, endDate);

        List<Map<String, Object>> list = new ArrayList<>();
        int totalCount = records.size();
        int foundCount = 0;

        for (ItemFindLog r : records) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", r.getDate().toString());
            item.put("itemName", r.getItemName());
            item.put("durationSeconds", r.getDurationSeconds());
            item.put("found", r.getFound());
            item.put("position", r.getPosition());
            list.add(item);

            if (Boolean.TRUE.equals(r.getFound())) {
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
