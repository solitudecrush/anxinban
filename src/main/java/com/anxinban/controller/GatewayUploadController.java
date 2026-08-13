package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.PageResult;
import com.anxinban.entity.AlarmEvent;
import com.anxinban.entity.FileMetadata;
import com.anxinban.mapper.AlarmEventRepository;
import com.anxinban.mapper.FileMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 网关图片上传控制器 — 专门接收网关设备通过 HTTP 上传的监控抓拍/门禁抓拍图片。
 *
 * <p>与前端管理端上传接口 {@link FileUploadController} 分离，提供独立的网关上传通道。
 * 上传的图片存储到 {@code {uploadRoot}/snapshot/} 目录，
 * 与告警 snapshotUrl 路径保持一致。</p>
 *
 * <h3>接口</h3>
 * <ul>
 *   <li>POST /api/gateway/upload-image — 网关上传图片</li>
 *   <li>GET  /api/gateway/images — 查询老人图片列表（供前端）</li>
 * </ul>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/gateway")
public class GatewayUploadController {

    private static final Logger log = LoggerFactory.getLogger(GatewayUploadController.class);

    @Value("${app.upload.dir:uploads}")
    private String uploadRoot;

    @Value("${app.file.allowed.types:image/jpeg,image/png,image/webp}")
    private String allowedTypesConfig;

    private final FileMetadataRepository fileMetadataRepository;
    private final AlarmEventRepository alarmEventRepository;

    @Autowired
    public GatewayUploadController(FileMetadataRepository fileMetadataRepository,
                                   AlarmEventRepository alarmEventRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
        this.alarmEventRepository = alarmEventRepository;
    }

    // ==================== 上传接口 ====================

    /**
     * 网关图片上传接口。
     *
     * <p>接收 multipart/form-data 请求，将图片保存到本地并按日期分目录，
     * 同时将文件元数据写入数据库。</p>
     *
     * <p>curl 示例：</p>
     * <pre>{@code
     * curl -X POST http://elderlyweb.cn/api/gateway/upload-image \
     *   -F "file=@snapshot.jpg" \
     *   -F "elderId=elder_001" \
     *   -F "gatewayId=gateway_001" \
     *   -F "cameraId=cam_01" \
     *   -F "alarmType=intrusion"
     * }</pre>
     *
     * @param file      图片文件（必填）
     * @param elderId   老人 ID（必填）
     * @param gatewayId 网关设备 ID（可选）
     * @param cameraId  摄像头 ID（可选）
     * @param timestamp 抓拍时间 ISO 8601（可选，不传使用服务器当前时间）
     * @param alarmType 关联告警类型（可选）
     * @param alarmId   告警 ID（可选，传了就把 fileUrl 写入该告警的 snapshotUrl）
     * @return 上传结果（fileId, fileUrl, fileSize, createdAt）
     */
    @PostMapping("/upload-image")
    public ApiResponse<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("elderId") String elderId,
            @RequestParam(value = "timestamp", required = false) String timestamp,
            @RequestParam(value = "alarmType", required = false) String alarmType,
            @RequestParam(value = "alarmId", required = false) String alarmId) {

        // 1. 校验文件非空
        if (file.isEmpty()) {
            return ApiResponse.error(400, "文件不能为空");
        }

        // 2. 校验文件类型
        String contentType = file.getContentType();
        Set<String> allowedTypes = parseAllowedTypes();
        if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
            return ApiResponse.error(400, "不支持的文件类型: " + contentType + "，仅支持: " + String.join(", ", allowedTypes));
        }

        // 3. 生成文件名和路径
        String originalName = file.getOriginalFilename();
        String ext = extractExtension(originalName, contentType);
        String fileId = "file_" + UUID.randomUUID().toString().substring(0, 12);
        String relativePath = "/uploads/snapshot/" + fileId + ext;

        // 4. 解析抓拍时间
        LocalDateTime snapshotTime = parseSnapshotTime(timestamp);

        Path targetPath = null;
        try {
            // 5. 确保目录存在
            Path targetDir = Paths.get(uploadRoot, "snapshot");
            Files.createDirectories(targetDir);

            // 6. 保存文件到磁盘（不使用 REPLACE_EXISTING，fileId 12位确保不冲突）
            targetPath = targetDir.resolve(fileId + ext);
            Files.copy(file.getInputStream(), targetPath);

            // 7. 先创建/更新告警（告警是核心数据，优先保证）
            String newAlarmId = alarmId;
            if (alarmId != null && !alarmId.isEmpty()) {
                AlarmEvent alarm = alarmEventRepository.findByAlarmId(alarmId);
                if (alarm != null) {
                    alarm.setSnapshotUrl(relativePath);
                    alarm.setUpdatedAt(LocalDateTime.now());
                    alarmEventRepository.save(alarm);
                    log.info("告警关联图片: alarmId={}, snapshotUrl={}", alarmId, relativePath);
                }
            } else {
                // 自动创建门锁异常告警
                newAlarmId = "alarm_" + UUID.randomUUID().toString().substring(0, 8);
                AlarmEvent alarm = new AlarmEvent();
                alarm.setAlarmId(newAlarmId);
                alarm.setElderId(elderId);
                alarm.setDeviceId("");
                alarm.setType(alarmType != null && !alarmType.isEmpty() ? alarmType : "door_lock");
                alarm.setRiskLevel("high");
                alarm.setStatus("pending");
                alarm.setDescription(alarmType != null && alarmType.equals("intrusion") ? "闯入告警抓拍" : "门锁异常抓拍");
                alarm.setBuilding("");
                alarm.setRoomNumber("");
                alarm.setUnit("");
                alarm.setLocation("");
                alarm.setSnapshotUrl(relativePath);
                alarm.setHandlerId("");
                alarm.setHandlerName("");
                alarm.setHandleNote("");
                alarm.setOccurTime(snapshotTime);
                alarm.setHandleTime(LocalDateTime.of(1970, 1, 1, 0, 0, 0));
                alarm.setIsRead(false);
                alarm.setAppNotified(false);
                alarm.setCreatedAt(LocalDateTime.now());
                alarm.setUpdatedAt(LocalDateTime.now());
                alarmEventRepository.save(alarm);
                log.info("自动创建告警: alarmId={}, elderId={}, type={}, snapshotUrl={}", newAlarmId, elderId, alarm.getType(), relativePath);
            }

            // 8. 写入文件元数据（告警已确保存在，元数据失败不影响核心数据）
            FileMetadata meta = new FileMetadata();
            meta.setFileId(fileId);
            meta.setElderId(elderId);
            meta.setOriginalName(originalName);
            meta.setFilePath(relativePath);
            meta.setFileSize(file.getSize());
            meta.setContentType(contentType);
            meta.setAlarmType(alarmType != null && !alarmType.isEmpty() ? alarmType : null);
            meta.setSnapshotTime(snapshotTime);
            meta.setCreatedAt(LocalDateTime.now());
            fileMetadataRepository.save(meta);

            // 9. 构建响应
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("fileId", fileId);
            data.put("fileUrl", relativePath);
            data.put("fileSize", file.getSize());
            data.put("alarmId", newAlarmId);
            data.put("createdAt", meta.getCreatedAt().toString());
            return ApiResponse.success(data);

        } catch (IOException e) {
            // 文件写入失败，尝试清理
            if (targetPath != null) {
                try { Files.deleteIfExists(targetPath); } catch (IOException ignored) {}
            }
            log.error("网关图片保存失败: elderId={}, error={}", elderId, e.getMessage(), e);
            return ApiResponse.error(500, "文件保存失败: " + e.getMessage());
        } catch (Exception e) {
            // 数据库操作失败，清理已写入的磁盘文件
            if (targetPath != null) {
                try { Files.deleteIfExists(targetPath); } catch (IOException ignored) {}
            }
            log.error("网关数据保存失败: elderId={}, error={}", elderId, e.getMessage(), e);
            return ApiResponse.error(500, "数据保存失败: " + e.getMessage());
        }
    }

    // ==================== 查询接口 ====================

    /**
     * 查询老人图片列表（供 Web/App 前端展示）。
     *
     * <p>按创建时间降序返回分页结果。</p>
     *
     * @param elderId   老人 ID（必填）
     * @param page      页码，默认 1
     * @param pageSize  每页大小，默认 20
     * @param alarmType 告警类型筛选（可选）
     * @return 分页文件元数据列表
     */
    @GetMapping("/images")
    public ApiResponse<PageResult<FileMetadata>> listImages(
            @RequestParam String elderId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String alarmType) {

        if (elderId == null || elderId.isEmpty()) {
            return ApiResponse.error(400, "elderId 不能为空");
        }

        PageRequest pageable = PageRequest.of(page - 1, pageSize);
        Page<FileMetadata> result;
        if (alarmType != null && !alarmType.isEmpty()) {
            result = fileMetadataRepository.findByElderIdAndAlarmTypeOrderByCreatedAtDesc(elderId, alarmType, pageable);
        } else {
            result = fileMetadataRepository.findByElderIdOrderByCreatedAtDesc(elderId, pageable);
        }

        PageResult<FileMetadata> pageResult = new PageResult<>(
                result.getContent(),
                result.getTotalElements(),
                page,
                pageSize
        );
        return ApiResponse.success(pageResult);
    }

    // ==================== 内部辅助 ====================

    /**
     * 解析允许的文件类型配置（逗号分隔 → Set）。
     */
    private Set<String> parseAllowedTypes() {
        Set<String> types = new HashSet<>();
        for (String t : allowedTypesConfig.split(",")) {
            String trimmed = t.trim().toLowerCase();
            if (!trimmed.isEmpty()) {
                types.add(trimmed);
            }
        }
        return types;
    }

    /**
     * 从原始文件名提取扩展名（含点），回退到 MIME 类型映射。
     */
    private String extractExtension(String originalName, String contentType) {
        if (originalName != null && originalName.contains(".")) {
            return originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }
        // 回退：根据 MIME 类型推断
        if (contentType != null) {
            switch (contentType.toLowerCase()) {
                case "image/jpeg": return ".jpg";
                case "image/png":  return ".png";
                case "image/webp": return ".webp";
            }
        }
        return ".jpg";
    }

    /**
     * 解析 ISO 8601 时间字符串，失败返回当前时间。
     */
    private LocalDateTime parseSnapshotTime(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            log.warn("抓拍时间解析失败: {}，使用服务器当前时间", timestamp);
            return LocalDateTime.now();
        }
    }
}
