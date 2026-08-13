package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.entity.FileMetadata;
import com.anxinban.mapper.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 抓拍图片查询控制器 — 供 App 端独立调用。
 *
 * <p>与告警解耦，直接查询 file_metadata 表，返回该老人所有上传过的抓拍文件。
 * 查询时校验文件是否真实存在，不存在的自动清理元数据，避免前端显示空白图片。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/snapshot")
public class SnapshotController {

    private final FileMetadataRepository fileMetadataRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadRoot;

    @Autowired
    public SnapshotController(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    /**
     * 查询老人抓拍列表（供 App 端展示）。
     * 自动跳过磁盘上不存在的文件并清理对应元数据。
     *
     * @param elderId  老人 ID（必填）
     * @param page     页码，默认 1
     * @param pageSize 每页大小，默认 20
     * @return 分页结果，含 fileId / fileUrl / fileSize / snapshotTime / alarmType / createdAt
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> list(
            @RequestParam String elderId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        Page<FileMetadata> result = fileMetadataRepository
                .findByElderIdOrderByCreatedAtDesc(elderId, PageRequest.of(page - 1, pageSize));

        Path uploadPath = Paths.get(uploadRoot).toAbsolutePath();
        List<Map<String, Object>> list = new ArrayList<>();
        int skipped = 0;

        for (FileMetadata m : result.getContent()) {
            String filePath = m.getFilePath();
            // 从相对路径 /uploads/snapshot/xxx.jpg 提取 snapshot/xxx.jpg
            String relative = filePath != null ? filePath.replaceFirst("^/uploads/", "") : "";
            Path diskPath = uploadPath.resolve(relative);

            if (Files.exists(diskPath)) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("fileId", m.getFileId());
                item.put("fileUrl", filePath);
                item.put("fileSize", m.getFileSize());
                item.put("snapshotTime", m.getSnapshotTime() != null ? m.getSnapshotTime().toString() : null);
                item.put("alarmType", m.getAlarmType());
                item.put("createdAt", m.getCreatedAt() != null ? m.getCreatedAt().toString() : null);
                list.add(item);
            } else {
                // 文件不存在，删除元数据
                fileMetadataRepository.delete(m);
                skipped++;
            }
        }

        if (skipped > 0) {
            // 清理后刷新 total
            long total = fileMetadataRepository.findByElderIdOrderByCreatedAtDesc(elderId,
                    PageRequest.of(0, 1)).getTotalElements();
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("list", list);
            data.put("total", total);
            return ApiResponse.success(data);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("list", list);
        data.put("total", result.getTotalElements());
        return ApiResponse.success(data);
    }
}
