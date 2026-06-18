package com.anxinban.controller;

/**
 * Sos REST 控制器，提供 Sos 相关的 HTTP API。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.SosDto;
import com.anxinban.service.SosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sos")
public class SosController {
    /** 字段含义待补充 */

    private final SosService sosService;

    @Autowired
    public SosController(SosService sosService) {
        this.sosService = sosService;
    }

    /**
     * 处理 POST  请求。
     *
     * @param sos sos 参数
     * @return 处理结果
     */
    @PostMapping
    public ApiResponse<SosDto> triggerSos(@RequestBody SosDto sos) {
        SosDto saved = sosService.triggerSos(sos);
        return ApiResponse.created(saved);
    }

    /**
     * 查询接口，处理 GET /{sosId} 请求。
     *
     * @param sosId sosId 参数
     * @return 处理结果
     */
    @GetMapping("/{sosId}")
    public ApiResponse<SosDto> getSos(@PathVariable String sosId) {
        SosDto sos = sosService.getSos(sosId);
        if (sos == null) {
            return ApiResponse.error(404, "求救记录不存在");
        }
        return ApiResponse.success(sos);
    }

    /**
     * 处理 GET /list 请求。
     *
     * @param elderId elderId 参数
     * @return 处理结果
     */
    @GetMapping("/list")
    public ApiResponse<List<SosDto>> listSos(@RequestParam String elderId) {
        return ApiResponse.success(sosService.listSosByElder(elderId));
    }

    /**
     * 处理 PUT /{sosId}/handle 请求。
     *
     * @param sosId sosId 参数
     * @param handlerId handlerId 参数
     * @return 处理结果
     */
    @PutMapping("/{sosId}/handle")
    public ApiResponse<SosDto> handleSos(@PathVariable String sosId, @RequestParam String handlerId) {
        SosDto updated = sosService.handleSos(sosId, handlerId);
        if (updated == null) {
            return ApiResponse.error(404, "求救记录不存在");
        }
        return ApiResponse.success(updated);
    }
}
