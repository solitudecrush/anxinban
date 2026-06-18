package com.anxinban.controller;

/**
 * LocalAgent REST 控制器，提供 LocalAgent 相关的 HTTP API。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.AgentInfoDto;
import com.anxinban.dto.AlarmDto;
import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.DeviceDto;
import com.anxinban.entity.AgentIntentLog;
import com.anxinban.entity.HomeControlLog;
import com.anxinban.service.AgentIntentLogService;
import com.anxinban.service.DeviceService;
import com.anxinban.service.HomeControlLogService;
import com.anxinban.service.LocalAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/local-agent")
public class LocalAgentController {
    private final LocalAgentService localAgentService;
    private final DeviceService deviceService;
    private final AgentIntentLogService agentIntentLogService;
    /** 业务服务实例 */
    private final HomeControlLogService homeControlLogService;

    @Autowired
    public LocalAgentController(LocalAgentService localAgentService, DeviceService deviceService,
                                AgentIntentLogService agentIntentLogService, HomeControlLogService homeControlLogService) {
        this.localAgentService = localAgentService;
        this.deviceService = deviceService;
        this.agentIntentLogService = agentIntentLogService;
        this.homeControlLogService = homeControlLogService;
    }

    /**
     * 处理 POST /heartbeat 请求。
     *
     * @param request request 参数
     * @return 处理结果
     */
    @PostMapping("/heartbeat")
    public ApiResponse<Void> heartbeat(@RequestBody AgentInfoDto request) {
        localAgentService.saveHeartbeat(request);
        return ApiResponse.success(null);
    }

    /**
     * 处理 POST /status 请求。
     *
     * @param request request 参数
     * @return 处理结果
     */
    @PostMapping("/status")
    public ApiResponse<Void> status(@RequestBody AgentInfoDto request) {
        localAgentService.saveStatus(request);
        return ApiResponse.success(null);
    }

    /**
     * 文件上传接口，处理 POST /data 请求。
     *
     * @param request request 参数
     * @return 处理结果
     */
    @PostMapping("/data")
    public ApiResponse<Void> uploadData(@RequestBody LocalAgentDataRequest request) {
        localAgentService.saveData(request.getAgentId(), request.getDeviceId(), request.getSensorDataList());
        if (request.getDeviceId() != null && request.getSensorDataList() != null) {
            deviceService.addSensorData(request.getDeviceId(), request.getSensorDataList());
        }
        return ApiResponse.success(null);
    }

    /**
     * 处理 POST /alarm-report 请求。
     *
     * @param request request 参数
     * @return 处理结果
     */
    @PostMapping("/alarm-report")
    public ApiResponse<AlarmDto> reportAlarm(@RequestBody LocalAgentAlarmRequest request) {
        AlarmDto alarm = localAgentService.reportAlarm(request.getAgentId(), request.getAlarm());
        return ApiResponse.created(alarm);
    }

    @GetMapping("/{agentId}/commands")
    public ApiResponse<List<DeviceDto.Command>> getPendingCommands(@PathVariable String agentId) {
        return ApiResponse.success(deviceService.getPendingCommands(agentId));
    }

    /**
     * 文件上传接口，处理 POST /intent 请求。
     *
     * @param intentLog intentLog 参数
     * @return 处理结果
     */
    @PostMapping("/intent")
    public ApiResponse<AgentIntentLog> uploadIntent(@RequestBody AgentIntentLog intentLog) {
        AgentIntentLog saved = agentIntentLogService.saveIntentLog(intentLog);
        return ApiResponse.created(saved);
    }

    /**
     * 文件上传接口，处理 POST /control 请求。
     *
     * @param controlLog controlLog 参数
     * @return 处理结果
     */
    @PostMapping("/control")
    public ApiResponse<HomeControlLog> uploadControl(@RequestBody HomeControlLog controlLog) {
        HomeControlLog saved = homeControlLogService.saveControlLog(controlLog);
        return ApiResponse.created(saved);
    }

    public static class LocalAgentDataRequest {
        private String agentId;
        private String deviceId;
        private List<DeviceDto.SensorData> sensorDataList;

            /**
             * 获取年龄。
             *
             * @return 年龄
             */
        /**
         * 查询接口，处理 请求  请求。
         * @return 处理结果
         */
        public String getAgentId() {
            return agentId;
        }

            /**
             * 设置年龄。
             *
             * @param agentId 年龄
             */
        /**
         * 处理 请求  请求。
         *
         * @param agentId agentId 参数
         */
        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

            /**
             * 获取关联设备 ID。
             *
             * @return 关联设备 ID
             */
        /**
         * 查询接口，处理 请求  请求。
         * @return 处理结果
         */
        public String getDeviceId() {
            return deviceId;
        }

            /**
             * 设置关联设备 ID。
             *
             * @param deviceId 关联设备 ID
             */
        /**
         * 处理 请求  请求。
         *
         * @param deviceId deviceId 参数
         */
        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

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

    public static class LocalAgentAlarmRequest {
        private String agentId;
        private AlarmDto alarm;

            /**
             * 获取年龄。
             *
             * @return 年龄
             */
        /**
         * 查询接口，处理 请求  请求。
         * @return 处理结果
         */
        public String getAgentId() {
            return agentId;
        }

            /**
             * 设置年龄。
             *
             * @param agentId 年龄
             */
        /**
         * 处理 请求  请求。
         *
         * @param agentId agentId 参数
         */
        public void setAgentId(String agentId) {
            this.agentId = agentId;
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
        public AlarmDto getAlarm() {
            return alarm;
        }

            /**
             * 设置字段含义待补充。
             *
             * @param alarm 字段含义待补充
             */
        /**
         * 处理 请求  请求。
         *
         * @param alarm alarm 参数
         */
        public void setAlarm(AlarmDto alarm) {
            this.alarm = alarm;
        }
    }
}
