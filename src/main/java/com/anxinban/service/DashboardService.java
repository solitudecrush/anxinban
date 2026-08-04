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
import com.anxinban.mapper.StaffUserRepository;
import com.anxinban.mapper.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final ElderUserRepository elderUserRepository;
    private final AlarmEventRepository alarmEventRepository;
    private final DeviceRepository deviceRepository;
    private final WorkOrderRepository workOrderRepository;
    private final StaffUserRepository staffUserRepository;

    @Autowired
    public DashboardService(ElderUserRepository elderUserRepository,
                            AlarmEventRepository alarmEventRepository,
                            DeviceRepository deviceRepository,
                            WorkOrderRepository workOrderRepository,
                            StaffUserRepository staffUserRepository) {
        this.elderUserRepository = elderUserRepository;
        this.alarmEventRepository = alarmEventRepository;
        this.deviceRepository = deviceRepository;
        this.workOrderRepository = workOrderRepository;
        this.staffUserRepository = staffUserRepository;
    }

    public DashboardStatsDto getStats() {
        DashboardStatsDto dto = new DashboardStatsDto();

        dto.setElderCount(elderUserRepository.count());
        dto.setDeviceCount(deviceRepository.count());
        dto.setAlarmCount(alarmEventRepository.count());
        dto.setWorkOrderCount(workOrderRepository.count());
        dto.setStaffCount(staffUserRepository.count());

        dto.setOnlineDeviceCount(deviceRepository.findAll().stream()
                .filter(d -> "online".equals(d.getStatus())).count());

        dto.setPendingAlarmCount(alarmEventRepository.countByStatus("pending"));

        dto.setPendingOrderCount(workOrderRepository.findAll().stream()
                .filter(o -> "待处理".equals(o.getStatus())).count());

        return dto;
    }
}
