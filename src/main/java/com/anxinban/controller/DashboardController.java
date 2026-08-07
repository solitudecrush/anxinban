package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.DashboardStatsDto;
import com.anxinban.entity.ElderDailyStats;
import com.anxinban.mapper.ElderDailyStatsRepository;
import com.anxinban.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final ElderDailyStatsRepository dailyStatsRepository;

    @Autowired
    public DashboardController(DashboardService dashboardService,
                               ElderDailyStatsRepository dailyStatsRepository) {
        this.dashboardService = dashboardService;
        this.dailyStatsRepository = dailyStatsRepository;
    }

    @GetMapping("/stats")
    public ApiResponse<DashboardStatsDto> getStats() {
        return ApiResponse.success(dashboardService.getStats());
    }

    @GetMapping("/buildings")
    public ApiResponse<List<String>> getBuildings() {
        List<String> buildings = List.of("1号楼", "2号楼", "3号楼", "5号楼", "6号楼");
        return ApiResponse.success(buildings);
    }

    /**
     * 社区健康趋势接口 —— 5 老人日均值平均，取最近 N 天。
     *
     * @param days 天数，默认 7
     * @return 按日期升序的每日社区平均健康数据
     */
    @GetMapping("/community-health")
    public ApiResponse<List<Map<String, Object>>> getCommunityHealth(
            @RequestParam(defaultValue = "7") int days) {

        String[] elders = {"elder_001", "elder_002", "elder_003", "elder_004", "elder_005"};
        LocalDate end = LocalDate.now().minusDays(1);
        LocalDate start = end.minusDays(days - 1);

        // 按日期分组聚合
        Map<LocalDate, List<ElderDailyStats>> byDate = new TreeMap<>();
        for (String eid : elders) {
            List<ElderDailyStats> list = dailyStatsRepository
                    .findByElderIdAndStatDateBetweenOrderByStatDateAsc(eid, start, end);
            for (ElderDailyStats s : list) {
                byDate.computeIfAbsent(s.getStatDate(), k -> new ArrayList<>()).add(s);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<LocalDate, List<ElderDailyStats>> entry : byDate.entrySet()) {
            List<ElderDailyStats> dayStats = entry.getValue();
            if (dayStats.isEmpty()) continue;

            double avgHr = avg(dayStats, s -> s.getAvgHr());
            double avgSpo2 = avg(dayStats, s -> s.getAvgSpo2());
            double avgTemp = avg(dayStats, s -> s.getAvgTemp());
            // 取最高 anxiety_score 对应的 tag
            String tag = dayStats.stream()
                    .filter(s -> s.getDailyTag() != null)
                    .max(Comparator.comparing(s -> s.getAnxietyScore() != null ? s.getAnxietyScore() : 0))
                    .map(ElderDailyStats::getDailyTag).orElse("normal");

            Map<String, Object> point = new LinkedHashMap<>();
            point.put("statDate", entry.getKey().toString());
            point.put("avgHr", round(avgHr, 1));
            point.put("avgSpo2", round(avgSpo2, 1));
            point.put("avgTemp", round(avgTemp, 1));
            point.put("dailyTag", tag);
            result.add(point);
        }

        return ApiResponse.success(result);
    }

    private double avg(List<ElderDailyStats> list, java.util.function.Function<ElderDailyStats, BigDecimal> getter) {
        return list.stream()
                .map(getter)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0);
    }

    private double round(double v, int scale) {
        return new BigDecimal(v).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }
}
