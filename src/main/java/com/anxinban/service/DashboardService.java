package com.anxinban.service;

/**
 * Dashboard 业务服务类，处理 Dashboard 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.DashboardStatsDto;
import com.anxinban.mapper.AlarmEventRepository;
import com.anxinban.mapper.DeviceRepository;
import com.anxinban.mapper.ElderUserRepository;
import com.anxinban.mapper.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DashboardService {
    private final ElderUserRepository elderUserRepository;
    private final AlarmEventRepository alarmEventRepository;
    private final DeviceRepository deviceRepository;
    /** 排序 */
    private final WorkOrderRepository workOrderRepository;

    @Autowired
    public DashboardService(ElderUserRepository elderUserRepository,
                            AlarmEventRepository alarmEventRepository,
                            DeviceRepository deviceRepository,
                            WorkOrderRepository workOrderRepository) {
        this.elderUserRepository = elderUserRepository;
        this.alarmEventRepository = alarmEventRepository;
        this.deviceRepository = deviceRepository;
        this.workOrderRepository = workOrderRepository;
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public DashboardStatsDto getStats() {
        DashboardStatsDto dto = new DashboardStatsDto();
        dto.setElderTotal(elderUserRepository.count());
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        dto.setTodayAlarmCount(alarmEventRepository.countByCreatedAtAfter(todayStart));
        dto.setTodayIntrusionCount(alarmEventRepository.countByTypeAndCreatedAtAfter("intrusion", todayStart));
        dto.setOnlineDeviceCount(deviceRepository.findAll().stream().filter(d -> "online".equals(d.getStatus())).count());
        dto.setPendingOrderCount(workOrderRepository.findAll().stream().filter(o -> "待分配".equals(o.getStatus())).count());
        dto.setHealthAbnormalCount(alarmEventRepository.findAll().stream()
                .filter(a -> a.getType() != null && (a.getType().contains("heart_rate") || a.getType().contains("blood_pressure") || a.getType().contains("temperature") || a.getType().contains("fall") || a.getType().contains("inactive")))
                .filter(a -> a.getCreatedAt() != null && a.getCreatedAt().isAfter(todayStart))
                .map(a -> a.getElderId())
                .distinct()
                .count());
        return dto;
    }
}
