package com.anxinban.service;

/**
 * Device 业务服务类，处理 Device 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.DeviceDto;
import com.anxinban.dto.PageResult;
import com.anxinban.entity.Device;
import com.anxinban.entity.SensorData;
import com.anxinban.mapper.DeviceRepository;
import com.anxinban.mapper.ElderUserRepository;
import com.anxinban.mapper.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final SensorDataRepository sensorDataRepository;
    /** 数据访问仓库，用于持久化操作 */
    private final ElderUserRepository elderUserRepository;
    private final Map<String, List<DeviceDto.Command>> commandStore = new ConcurrentHashMap<>();

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, SensorDataRepository sensorDataRepository, ElderUserRepository elderUserRepository) {
        this.deviceRepository = deviceRepository;
        this.sensorDataRepository = sensorDataRepository;
        this.elderUserRepository = elderUserRepository;
    }

        /**
         * registerDevice 方法。
         *
         * @param device 字段含义待补充
         */
    public DeviceDto registerDevice(DeviceDto device) {
        if (device.getDeviceId() == null || device.getDeviceId().isEmpty()) {
            throw new IllegalArgumentException("deviceId is required");
        }
        Device entity = convertToEntity(device);
        entity.setStatus(device.getStatus() == null ? "online" : device.getStatus());
        entity.setLastOnlineTime(device.getLastHeartbeat() != null ? LocalDateTime.parse(device.getLastHeartbeat().substring(0, 19)) : LocalDateTime.now());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        Device saved = deviceRepository.save(entity);
        return convertToDto(saved);
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public DeviceDto getDevice(String deviceId) {
        Device entity = deviceRepository.findByDeviceId(deviceId);
        return entity != null ? convertToDto(entity) : null;
    }

        /**
         * listDevices 方法。
         *
         * @param size 大小
         */
    public PageResult<DeviceDto> listDevices(String status, String deviceType, String location, int page, int size) {
        List<Device> entities;
        if (status != null && !status.isEmpty()) {
            entities = deviceRepository.findByStatus(status);
        } else if (deviceType != null && !deviceType.isEmpty()) {
            entities = deviceRepository.findByDeviceType(deviceType);
        } else if (location != null && !location.isEmpty()) {
            entities = deviceRepository.findByLocation(location);
        } else {
            entities = deviceRepository.findAll();
        }
        List<DeviceDto> dtos = entities.stream().map(this::convertToDto).collect(Collectors.toList());
        long total = dtos.size();
        List<DeviceDto> paginated = paginate(dtos, page, size);
        return new PageResult<>(paginated, total, page, size);
    }

        /**
         * listDevicesByElder 方法。
         *
         * @param elderId 关联老人用户 ID
         */
    public List<DeviceDto> listDevicesByElder(String elderId) {
        return deviceRepository.findByElderId(elderId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

        /**
         * updateDeviceStatus 方法。
         *
         * @param lastHeartbeat 字段含义待补充
         */
    public DeviceDto updateDeviceStatus(String deviceId, String status, String lastHeartbeat) {
        Device existing = deviceRepository.findByDeviceId(deviceId);
        if (existing == null) {
            return null;
        }
        if (status != null) {
            existing.setStatus(status);
        }
        if (lastHeartbeat != null) {
            existing.setLastOnlineTime(LocalDateTime.parse(lastHeartbeat.substring(0, 19)));
        }
        existing.setUpdateTime(LocalDateTime.now());
        Device saved = deviceRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * addSensorData 方法。
         *
         * @param List 数据列表
         * @param sensorDataList 数据载荷
         */
    public boolean addSensorData(String deviceId, List<DeviceDto.SensorData> sensorDataList) {
        Device existing = deviceRepository.findByDeviceId(deviceId);
        if (existing == null) {
            return false;
        }
        if (sensorDataList == null || sensorDataList.isEmpty()) {
            return true;
        }
        for (DeviceDto.SensorData data : sensorDataList) {
            SensorData entity = new SensorData();
            entity.setElderId(existing.getElderId());
            entity.setDeviceId(deviceId);
            entity.setSensorType(data.getSensorType());
            entity.setValue(data.getValue() != null ? Double.valueOf(data.getValue().toString()) : null);
            entity.setUnit(data.getUnit());
            entity.setIsAbnormal("alarm".equals(data.getStatus()));
            entity.setTimestamp(data.getTimestamp() != null ? LocalDateTime.parse(data.getTimestamp().substring(0, 19)) : LocalDateTime.now());
            entity.setCreatedAt(LocalDateTime.now());
            sensorDataRepository.save(entity);
        }
        return true;
    }

    public DeviceDto.Command sendCommand(String deviceId, DeviceDto.Command command) {
        Device existing = deviceRepository.findByDeviceId(deviceId);
        if (existing == null) {
            return null;
        }
        commandStore.computeIfAbsent(deviceId, id -> new ArrayList<>()).add(command);
        return command;
    }

    public List<DeviceDto.SensorData> querySensorData(String deviceId, String sensorType, String startTime, String endTime, int page, int size) {
        if (deviceRepository.findByDeviceId(deviceId) == null) {
            return null;
        }
        List<SensorData> entities;
        LocalDateTime start = startTime != null ? LocalDateTime.parse(startTime.substring(0, 19)) : null;
        LocalDateTime end = endTime != null ? LocalDateTime.parse(endTime.substring(0, 19)) : null;
        if (sensorType != null && start != null && end != null) {
            entities = sensorDataRepository.findByDeviceIdAndSensorTypeAndTimestampBetween(deviceId, sensorType, start, end);
        } else if (sensorType != null) {
            entities = sensorDataRepository.findByDeviceIdAndSensorType(deviceId, sensorType);
        } else if (start != null && end != null) {
            entities = sensorDataRepository.findByDeviceIdAndTimestampBetween(deviceId, start, end);
        } else {
            entities = sensorDataRepository.findByDeviceId(deviceId);
        }
        List<DeviceDto.SensorData> dtos = entities.stream().map(this::convertSensorDataToDto).collect(Collectors.toList());
        return paginate(dtos, page, size);
    }

    public List<DeviceDto.Command> getPendingCommands(String agentId) {
        return commandStore.values().stream()
                .flatMap(List::stream)
                .filter(cmd -> "pending".equals(cmd.getStatus()))
                .collect(Collectors.toList());
    }

        /**
         * convertToDto 方法。
         *
         * @param entity 字段含义待补充
         */
    private DeviceDto convertToDto(Device entity) {
        DeviceDto dto = new DeviceDto();
        dto.setDeviceId(entity.getDeviceId());
        dto.setElderId(entity.getElderId());
        dto.setDeviceType(entity.getDeviceType());
        dto.setDeviceName(entity.getDeviceName());
        dto.setLocation(entity.getLocation());
        dto.setBuilding(entity.getBuilding());
        dto.setRoom(entity.getRoom());
        dto.setStatus(entity.getStatus());
        dto.setBatteryLevel(entity.getBatteryLevel());
        dto.setLastHeartbeat(entity.getLastOnlineTime() != null ? entity.getLastOnlineTime().toString() : null);
        if (entity.getElderId() != null) {
            var elder = elderUserRepository.findByElderId(entity.getElderId());
            if (elder != null) {
                dto.setElderName(elder.getName());
            }
        }
        return dto;
    }

        /**
         * convertToEntity 方法。
         *
         * @param dto 字段含义待补充
         */
    private Device convertToEntity(DeviceDto dto) {
        Device entity = new Device();
        entity.setDeviceId(dto.getDeviceId());
        entity.setElderId(dto.getElderId());
        entity.setDeviceType(dto.getDeviceType());
        entity.setDeviceName(dto.getDeviceName());
        entity.setLocation(dto.getLocation());
        entity.setBuilding(dto.getBuilding());
        entity.setRoom(dto.getRoom());
        entity.setStatus(dto.getStatus());
        entity.setBatteryLevel(dto.getBatteryLevel());
        return entity;
    }

    private DeviceDto.SensorData convertSensorDataToDto(SensorData entity) {
        DeviceDto.SensorData dto = new DeviceDto.SensorData();
        dto.setSensorId("SEN-" + entity.getId());
        dto.setSensorType(entity.getSensorType());
        dto.setValue(entity.getValue());
        dto.setUnit(entity.getUnit());
        dto.setTimestamp(entity.getTimestamp().toString());
        dto.setStatus(entity.getIsAbnormal() ? "alarm" : "normal");
        return dto;
    }

        /**
         * paginate 方法。
         *
         * @param size 大小
         */
    private <T> List<T> paginate(List<T> items, int page, int size) {
        int from = Math.max((page - 1) * size, 0);
        int to = Math.min(from + size, items.size());
        if (from > items.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(items.subList(from, to));
    }
}
