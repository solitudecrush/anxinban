package com.anxinban.controller;

/**
 * Notification REST 控制器，提供 Notification 相关的 HTTP API。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.NotificationDto;
import com.anxinban.dto.PageResult;
import com.anxinban.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    /** 字段含义待补充 */

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/list")
    public ApiResponse<PageResult<NotificationDto>> listNotifications(
            @RequestParam String userId,
            @RequestParam(required = false) String userType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResult<NotificationDto> result = notificationService.listNotifications(userId, userType, page, pageSize);
        return ApiResponse.success(result);
    }

    /**
     * 处理 POST /{notificationId}/read 请求。
     *
     * @param notificationId notificationId 参数
     * @return 处理结果
     */
    @PostMapping("/{notificationId}/read")
    public ApiResponse<Void> markAsRead(@PathVariable String notificationId) {
        boolean success = notificationService.markAsRead(notificationId);
        if (!success) {
            return ApiResponse.error(404, "通知不存在");
        }
        return ApiResponse.success(null);
    }

    /**
     * 处理 POST /read-all 请求。
     *
     * @param userId userId 参数
     * @param userType userType 参数
     * @return 处理结果
     */
    @PostMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(@RequestParam String userId, @RequestParam(required = false) String userType) {
        notificationService.markAllAsRead(userId, userType);
        return ApiResponse.success(null);
    }

    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> getUnreadCount(
            @RequestParam String userId,
            @RequestParam(required = false) String userType) {
        long count = notificationService.getUnreadCount(userId, userType);
        return ApiResponse.success(Map.of("count", count));
    }
}
