package com.anxinban.controller;

/**
 * HealthRecord REST 控制器，提供 HealthRecord 相关的 HTTP API。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.HealthRecordDto;
import com.anxinban.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health-record")
public class HealthRecordController {
    /** 字段含义待补充 */

    private final HealthRecordService healthRecordService;

    @Autowired
    public HealthRecordController(HealthRecordService healthRecordService) {
        this.healthRecordService = healthRecordService;
    }

    /**
     * 新增接口，处理 POST  请求。
     *
     * @param record record 参数
     * @return 处理结果
     */
    @PostMapping
    public ApiResponse<HealthRecordDto> saveRecord(@RequestBody HealthRecordDto record) {
        return ApiResponse.success(healthRecordService.createOrUpdateRecord(record));
    }

    /**
     * 查询接口，处理 GET /by-elder/{elderId} 请求。
     *
     * @param elderId elderId 参数
     * @return 处理结果
     */
    @GetMapping("/by-elder/{elderId}")
    public ApiResponse<HealthRecordDto> getRecordByElder(@PathVariable String elderId) {
        HealthRecordDto record = healthRecordService.getRecordByElderId(elderId);
        if (record == null) {
            return ApiResponse.error(404, "健康档案不存在");
        }
        return ApiResponse.success(record);
    }

    /**
     * 查询接口，处理 GET /{recordId} 请求。
     *
     * @param recordId recordId 参数
     * @return 处理结果
     */
    @GetMapping("/{recordId}")
    public ApiResponse<HealthRecordDto> getRecord(@PathVariable String recordId) {
        HealthRecordDto record = healthRecordService.getRecord(recordId);
        if (record == null) {
            return ApiResponse.error(404, "健康档案不存在");
        }
        return ApiResponse.success(record);
    }
}
