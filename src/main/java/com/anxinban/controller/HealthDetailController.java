package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * 健康详情 REST 控制器 — 提供音乐、陪伴、物品寻找三个维度的详情数据。
 *
 * <p>统一前缀：/api/health，所有接口支持 startDate / endDate 时间筛选，
 * 默认取最近 7 天数据。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/health")
public class HealthDetailController {

    private final MusicDetailService musicDetailService;
    private final CompanionDetailService companionDetailService;
    private final ItemFindDetailService itemFindDetailService;

    @Autowired
    public HealthDetailController(MusicDetailService musicDetailService,
                                  CompanionDetailService companionDetailService,
                                  ItemFindDetailService itemFindDetailService) {
        this.musicDetailService = musicDetailService;
        this.companionDetailService = companionDetailService;
        this.itemFindDetailService = itemFindDetailService;
    }

    /**
     * 音乐详情接口。
     *
     * <p>返回每日播放总时长列表，以及最近 7 天播放次数最多的 5 首歌。</p>
     * <p>支持 userId 和 elderId 两种参数名（elderId 为与看板接口保持一致的别名），
     * 两者都传时优先使用 userId，都不传时默认 "1"。</p>
     */
    @GetMapping("/music/detail")
    public ApiResponse<Map<String, Object>> getMusicDetail(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String elderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        String effectiveUserId = resolveUserId(userId, elderId);

        if (startDate == null) startDate = LocalDate.now().minusDays(6);
        if (endDate == null) endDate = LocalDate.now();

        Map<String, Object> data = musicDetailService.getMusicDetail(effectiveUserId, startDate, endDate);
        return ApiResponse.success(data);
    }

    /**
     * 陪伴对话详情接口。
     *
     * <p>返回每日情绪分布（各情绪标签出现次数）以及最近 10 条对话预览。</p>
     * <p>支持 userId 和 elderId 两种参数名（elderId 为与看板接口保持一致的别名），
     * 两者都传时优先使用 userId，都不传时默认 "1"。</p>
     */
    @GetMapping("/companion/detail")
    public ApiResponse<Map<String, Object>> getCompanionDetail(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String elderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        String effectiveUserId = resolveUserId(userId, elderId);

        if (startDate == null) startDate = LocalDate.now().minusDays(6);
        if (endDate == null) endDate = LocalDate.now();

        Map<String, Object> data = companionDetailService.getCompanionDetail(effectiveUserId, startDate, endDate);
        return ApiResponse.success(data);
    }

    /**
     * 物品寻找详情接口。
     *
     * <p>返回物品寻找历史记录列表、总次数和成功率。</p>
     * <p>支持 userId 和 elderId 两种参数名（elderId 为与看板接口保持一致的别名），
     * 两者都传时优先使用 userId，都不传时默认 "1"。</p>
     */
    @GetMapping("/item/detail")
    public ApiResponse<Map<String, Object>> getItemFindDetail(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String elderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        String effectiveUserId = resolveUserId(userId, elderId);

        if (startDate == null) startDate = LocalDate.now().minusDays(6);
        if (endDate == null) endDate = LocalDate.now();

        Map<String, Object> data = itemFindDetailService.getItemFindDetail(effectiveUserId, startDate, endDate);
        return ApiResponse.success(data);
    }

    /**
     * 解析有效的用户标识 —— 兼容 userId 和 elderId 两种参数名。
     *
     * <p>优先级：userId > elderId > 默认值 "1"。</p>
     *
     * @param userId  请求参数 userId（可能为 null 或空字符串）
     * @param elderId 请求参数 elderId（可能为 null 或空字符串）
     * @return 有效的用户标识，保证不为 null
     */
    private String resolveUserId(String userId, String elderId) {
        if (userId != null && !userId.isEmpty()) return userId;
        if (elderId != null && !elderId.isEmpty()) return elderId;
        return "1";
    }
}