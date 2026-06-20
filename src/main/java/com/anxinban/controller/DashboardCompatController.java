package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.mapper.AlarmEventRepository;
import com.anxinban.mapper.DeviceRepository;
import com.anxinban.mapper.ElderUserRepository;
import com.anxinban.mapper.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 比赛兼容层 - 大屏摘要兼容接口。
 *
 * <p>提供 GET /api/dashboard/summary，兼容队长 AI Mock Cloud 的返回格式。
 * 数据从数据库实时统计，不影响原有 /api/dashboard/stats。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardCompatController {

    @Autowired
    private ElderUserRepository elderUserRepository;

    @Autowired
    private AlarmEventRepository alarmEventRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    /**
     * 大屏摘要兼容接口。
     *
     * <p>与 /api/dashboard/stats 共存，返回格式兼容队长 Mock Cloud。
     * 所有数据从数据库实时统计。</p>
     *
     * @return 大屏摘要数据
     */
    @GetMapping("/summary")
    public ApiResponse<Map<String, Object>> getSummary() {
        Map<String, Object> data = new HashMap<>();

        // 老人总数
        long elderCount = elderUserRepository.count();
        data.put("elder_count", elderCount);

        // 高风险老人数（health_status = 'danger'）
        long highRiskCount = elderUserRepository.findByHealthStatus("danger").size();
        data.put("high_risk_count", highRiskCount);

        // 待处理告警数
        long pendingAlarmCount = alarmEventRepository.countByStatus("pending");
        data.put("pending_alarm_count", pendingAlarmCount);

        // 设备总数
        long totalDeviceCount = deviceRepository.count();
        data.put("total_device_count", totalDeviceCount);

        // 在线设备数
        long onlineDeviceCount = deviceRepository.findByStatus("online").size();
        data.put("online_device_count", onlineDeviceCount);

        // 离线设备数
        long offlineDeviceCount = deviceRepository.findByStatus("offline").size();
        data.put("offline_device_count", offlineDeviceCount);

        // 工单总数
        long workOrderCount = workOrderRepository.count();
        data.put("work_order_count", workOrderCount);

        // 本月工单数：统计当月 1 日 00:00 至今创建的工单
        LocalDateTime monthStart = LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        long monthWorkOrderCount = workOrderRepository.countByCreatedAtAfter(monthStart);
        // 如果本月工单为 0（可能是历史数据），回退到工单总数
        if (monthWorkOrderCount == 0) {
            monthWorkOrderCount = workOrderCount;
        }
        data.put("month_work_order_count", monthWorkOrderCount);

        return ApiResponse.success(data);
    }
}
