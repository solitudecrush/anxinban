package com.anxinban.controller;

/**
 * FileUpload REST 控制器，提供 FileUpload 相关的 HTTP API。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    /**
     * 文件上传接口，处理 POST /avatar 请求。
     *
     * @param file file 参数
     * @return 处理结果
     */
    @PostMapping("/avatar")
    public ApiResponse<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String url = "/uploads/avatar/" + UUID.randomUUID() + ".jpg";
        return ApiResponse.success(Map.of("url", url));
    }

    /**
     * 文件上传接口，处理 POST /snapshot 请求。
     *
     * @param file file 参数
     * @return 处理结果
     */
    @PostMapping("/snapshot")
    public ApiResponse<Map<String, String>> uploadSnapshot(@RequestParam("file") MultipartFile file) {
        String url = "/uploads/snapshot/" + UUID.randomUUID() + ".jpg";
        return ApiResponse.success(Map.of("url", url));
    }
}
