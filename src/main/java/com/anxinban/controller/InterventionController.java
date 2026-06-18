package com.anxinban.controller;

/**
 * Intervention REST 控制器，提供 Intervention 相关的 HTTP API。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.InterventionDto;
import com.anxinban.service.InterventionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/intervention")
public class InterventionController {
    /** 字段含义待补充 */

    private final InterventionService interventionService;

    @Autowired
    public InterventionController(InterventionService interventionService) {
        this.interventionService = interventionService;
    }

    /**
     * 新增接口，处理 POST  请求。
     *
     * @param intervention intervention 参数
     * @return 处理结果
     */
    @PostMapping
    public ApiResponse<InterventionDto> createIntervention(@RequestBody InterventionDto intervention) {
        InterventionDto saved = interventionService.createIntervention(intervention);
        return ApiResponse.created(saved);
    }

    /**
     * 查询接口，处理 GET /{interventionId} 请求。
     *
     * @param interventionId interventionId 参数
     * @return 处理结果
     */
    @GetMapping("/{interventionId}")
    public ApiResponse<InterventionDto> getIntervention(@PathVariable String interventionId) {
        InterventionDto intervention = interventionService.getIntervention(interventionId);
        if (intervention == null) {
            return ApiResponse.error(404, "干预记录不存在");
        }
        return ApiResponse.success(intervention);
    }

    @GetMapping("/list")
    public ApiResponse<List<InterventionDto>> listInterventions(
            @RequestParam(required = false) String elderId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(interventionService.listInterventions(elderId, status, priority, page, size));
    }

    /**
     * 更新接口，处理 PUT /{interventionId} 请求。
     *
     * @param interventionId interventionId 参数
     * @param intervention intervention 参数
     * @return 处理结果
     */
    @PutMapping("/{interventionId}")
    public ApiResponse<InterventionDto> updateIntervention(@PathVariable String interventionId, @RequestBody InterventionDto intervention) {
        InterventionDto updated = interventionService.updateIntervention(interventionId, intervention);
        if (updated == null) {
            return ApiResponse.error(404, "干预记录不存在");
        }
        return ApiResponse.success(updated);
    }

    /**
     * 处理 PUT /{interventionId}/complete 请求。
     *
     * @param interventionId interventionId 参数
     * @param request request 参数
     * @return 处理结果
     */
    @PutMapping("/{interventionId}/complete")
    public ApiResponse<InterventionDto> completeIntervention(@PathVariable String interventionId, @RequestBody InterventionCompleteRequest request) {
        InterventionDto updated = interventionService.completeIntervention(interventionId, request.getStatus(), request.getResult(), request.getCompleteTime());
        if (updated == null) {
            return ApiResponse.error(404, "干预记录不存在");
        }
        return ApiResponse.success(updated);
    }

    public static class InterventionCompleteRequest {
        private String status;
        private String result;
        private String completeTime;

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
             * 获取结果。
             *
             * @return 结果
             */
        /**
         * 查询接口，处理 请求  请求。
         * @return 处理结果
         */
        public String getResult() {
            return result;
        }

            /**
             * 设置结果。
             *
             * @param result 结果
             */
        /**
         * 处理 请求  请求。
         *
         * @param result result 参数
         */
        public void setResult(String result) {
            this.result = result;
        }

            /**
             * 获取时间。
             *
             * @return 时间
             */
        /**
         * 查询接口，处理 请求  请求。
         * @return 处理结果
         */
        public String getCompleteTime() {
            return completeTime;
        }

            /**
             * 设置时间。
             *
             * @param completeTime 时间
             */
        /**
         * 处理 请求  请求。
         *
         * @param completeTime completeTime 参数
         */
        public void setCompleteTime(String completeTime) {
            this.completeTime = completeTime;
        }
    }
}
