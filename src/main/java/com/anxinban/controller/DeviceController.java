package com.anxinban.controller;

/**
 * Device REST 控制器，提供 Device 相关的 HTTP API。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.DeviceDto;
import com.anxinban.dto.PageResult;
import com.anxinban.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/device")
public class DeviceController {
    /** 字段含义待补充 */

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * 注册接口，处理 POST /register 请求。
     *
     * @param request request 参数
     * @return 处理结果
     */
    @PostMapping("/register")
    public ApiResponse<DeviceDto> registerDevice(@RequestBody DeviceDto request) {
        DeviceDto saved = deviceService.registerDevice(request);
        return ApiResponse.created(saved);
    }

    /**
     * 查询接口，处理 GET /{deviceId} 请求。
     *
     * @param deviceId deviceId 参数
     * @return 处理结果
     */
    @GetMapping("/{deviceId}")
    public ApiResponse<DeviceDto> getDevice(@PathVariable String deviceId) {
        DeviceDto device = deviceService.getDevice(deviceId);
        if (device == null) {
            return ApiResponse.error(404, "设备不存在");
        }
        return ApiResponse.success(device);
    }

    @GetMapping("/list")
    public ApiResponse<PageResult<DeviceDto>> listDevices(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResult<DeviceDto> result = deviceService.listDevices(status, deviceType, location, page, pageSize);
        return ApiResponse.success(result);
    }

    @PutMapping("/{deviceId}/status")
    public ApiResponse<DeviceDto> updateStatus(@PathVariable String deviceId,
                                                @RequestBody DeviceStatusRequest request) {
        DeviceDto device = deviceService.updateDeviceStatus(deviceId, request.getStatus(), request.getLastHeartbeat());
        if (device == null) {
            return ApiResponse.error(404, "设备不存在");
        }
        return ApiResponse.success(device);
    }

    @PostMapping("/{deviceId}/sensor-data")
    public ApiResponse<Void> uploadSensorData(@PathVariable String deviceId,
                                              @RequestBody SensorDataRequest request) {
        boolean result = deviceService.addSensorData(deviceId, request.getSensorDataList());
        if (!result) {
            return ApiResponse.error(404, "设备不存在");
        }
        return ApiResponse.success(null);
    }

    @PostMapping("/{deviceId}/command")
    public ApiResponse<DeviceDto.Command> sendCommand(@PathVariable String deviceId,
                                                       @RequestBody DeviceCommandRequest request) {
        DeviceDto.Command command = new DeviceDto.Command();
        command.setCommandId(request.getCommandId() == null ? "CMD-" + Instant.now().toEpochMilli() : request.getCommandId());
        command.setDeviceId(deviceId);
        command.setCommandType(request.getCommandType());
        command.setParameters(request.getParameters());
        command.setStatus("pending");
        command.setTimestamp(Instant.now().toString());
        DeviceDto.Command saved = deviceService.sendCommand(deviceId, command);
        if (saved == null) {
            return ApiResponse.error(404, "设备不存在");
        }
        return ApiResponse.success(saved);
    }

    @GetMapping("/{deviceId}/sensor-data")
    public ApiResponse<List<DeviceDto.SensorData>> querySensorData(@PathVariable String deviceId,
                                                                   @RequestParam(required = false) String sensorType,
                                                                   @RequestParam(required = false) String startTime,
                                                                   @RequestParam(required = false) String endTime,
                                                                   @RequestParam(defaultValue = "1") int page,
                                                                   @RequestParam(defaultValue = "20") int pageSize) {
        List<DeviceDto.SensorData> data = deviceService.querySensorData(deviceId, sensorType, startTime, endTime, page, pageSize);
        if (data == null) {
            return ApiResponse.error(404, "设备不存在");
        }
        return ApiResponse.success(data);
    }

    public static class DeviceStatusRequest {
        private String status;
        private String lastHeartbeat;

            /**
             * 获取状态标识。
             *
             * @return 状态标识
             */
        /**
         * 查询接口，处理 请求  请求。
         * @return 处理结果
         */
        public String getStatus() {
            return status;
        }

            /**
             * 设置状态标识。
             *
             * @param status 状态标识
             */
        /**
         * 处理 请求  请求。
         *
         * @param status status 参数
         */
        public void setStatus(String status) {
            this.status = status;
        }

            /**
             * 获取字段含义待补充。
             *
             * @return 字段含义待补充
             */
        /**
         * 查询接口，处理 请求  请求。
         * @return 处理结果
         */
        public String getLastHeartbeat() {
            return lastHeartbeat;
        }

            /**
             * 设置字段含义待补充。
             *
             * @param lastHeartbeat 字段含义待补充
             */
        /**
         * 处理 请求  请求。
         *
         * @param lastHeartbeat lastHeartbeat 参数
         */
        public void setLastHeartbeat(String lastHeartbeat) {
            this.lastHeartbeat = lastHeartbeat;
        }
    }

    public static class SensorDataRequest {
        private List<DeviceDto.SensorData> sensorDataList;

        public List<DeviceDto.SensorData> getSensorDataList() {
            return sensorDataList;
        }

            /**
             * 设置数据载荷。
             *
             * @param sensorDataList 数据载荷
             */
        /**
         * 处理 请求  请求。
         *
         * @param sensorDataList sensorDataList 参数
         */
        public void setSensorDataList(List<DeviceDto.SensorData> sensorDataList) {
            this.sensorDataList = sensorDataList;
        }
    }

    public static class DeviceCommandRequest {
        private String commandId;
        private String commandType;
        private Map<String, Object> parameters;

            /**
             * 获取控制命令。
             *
             * @return 控制命令
             */
        /**
         * 查询接口，处理 请求  请求。
         * @return 处理结果
         */
        public String getCommandId() {
            return commandId;
        }

            /**
             * 设置控制命令。
             *
             * @param commandId 控制命令
             */
        /**
         * 处理 请求  请求。
         *
         * @param commandId commandId 参数
         */
        public void setCommandId(String commandId) {
            this.commandId = commandId;
        }

            /**
             * 获取控制命令。
             *
             * @return 控制命令
             */
        /**
         * 查询接口，处理 请求  请求。
         * @return 处理结果
         */
        public String getCommandType() {
            return commandType;
        }

            /**
             * 设置控制命令。
             *
             * @param commandType 控制命令
             */
        /**
         * 处理 请求  请求。
         *
         * @param commandType commandType 参数
         */
        public void setCommandType(String commandType) {
            this.commandType = commandType;
        }

            /**
             * 获取字段含义待补充。
             *
             * @return 字段含义待补充
             */
        /**
         * 查询接口，处理 请求  请求。
         * @return 处理结果
         */
        public Map<String, Object> getParameters() {
            return parameters;
        }

            /**
             * 设置字段含义待补充。
             *
             * @param parameters 字段含义待补充
             */
        /**
         * 处理 请求  请求。
         *
         * @param parameters parameters 参数
         */
        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }
    }
}
