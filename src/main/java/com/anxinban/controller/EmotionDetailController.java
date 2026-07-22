package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.EmotionDetailDTO;
import com.anxinban.service.EmotionDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 情绪分析 REST 控制器 — 为前端"图表"页面情绪分析板块提供接口。
 *
 * <p>路由：</p>
 * <ul>
 *   <li>GET /api/health/emotion/analysis?dimension=day|week|month&elderId=xxx — 情绪分析结论（需求二）</li>
 *   <li>GET /api/health/emotion/detail?dimension=day|week|month&elderId=xxx — 情绪分析详情（需求三）</li>
 * </ul>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/health/emotion")
public class EmotionDetailController {

    private static final Logger log = LoggerFactory.getLogger(EmotionDetailController.class);
    private final EmotionDetailService emotionDetailService;

    @Autowired
    public EmotionDetailController(EmotionDetailService emotionDetailService) {
        this.emotionDetailService = emotionDetailService;
    }

    /**
     * 情绪分析结论接口（需求二）。
     *
     * <p>根据时间维度返回情绪标签和评分：
     * <ul>
     *   <li>日维度 → "平稳"，评分 7.5</li>
     *   <li>周/月维度 → "焦虑"，评分 3.2</li>
     * </ul>
     *
     * @param dimension 时间维度（day/week/month）
     * @param elderId   老人 ID
     * @return 情绪分析结论
     */
    @GetMapping("/analysis")
    public ApiResponse<Map<String, Object>> getEmotionAnalysis(
            @RequestParam(defaultValue = "week") String dimension,
            @RequestParam String elderId) {

        if (elderId == null || elderId.isEmpty()) {
            return ApiResponse.error(400, "elderId 不能为空");
        }

        log.info("情绪分析请求: elderId={}, dimension={}", elderId, dimension);

        EmotionDetailDTO detail = emotionDetailService.getEmotionDetail(elderId, dimension);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dimension", detail.getDimension());
        result.put("dateRange", detail.getDateRange());
        result.put("emotionLabel", detail.getEmotionLabel());
        result.put("emotionScore", detail.getEmotionScore());

        return ApiResponse.success(result);
    }

    /**
     * 情绪分析详情接口（需求三 — "查看详情"）。
     *
     * <p>返回完整的情绪分析详情，包括情绪标签、评分、分析文本、
     * 情绪占比分布、关键事件及建议提醒。</p>
     *
     * @param dimension 时间维度（day/week/month）
     * @param elderId   老人 ID
     * @return 情绪分析详情
     */
    @GetMapping("/detail")
    public ApiResponse<EmotionDetailDTO> getEmotionDetail(
            @RequestParam(defaultValue = "week") String dimension,
            @RequestParam String elderId) {

        if (elderId == null || elderId.isEmpty()) {
            return ApiResponse.error(400, "elderId 不能为空");
        }

        log.info("情绪详情请求: elderId={}, dimension={}", elderId, dimension);

        EmotionDetailDTO detail = emotionDetailService.getEmotionDetail(elderId, dimension);
        return ApiResponse.success(detail);
    }
}
