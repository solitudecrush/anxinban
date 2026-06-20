package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.entity.CameraViewRecord;
import com.anxinban.service.CameraViewRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 监控查看记录 REST 控制器。
 *
 * <p>对应数据字典：camera_view_record</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/camera-view-record")
public class CameraViewController {

    private final CameraViewRecordService service;

    @Autowired
    public CameraViewController(CameraViewRecordService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse<CameraViewRecord> create(@RequestBody CameraViewRecord record) {
        CameraViewRecord saved = service.save(record);
        return saved != null ? ApiResponse.created(saved) : ApiResponse.error(500, "保存失败");
    }

    @GetMapping("/list")
    public ApiResponse<List<CameraViewRecord>> list(@RequestParam String cameraRequestId) {
        return ApiResponse.success(service.listByCameraRequest(cameraRequestId));
    }
}
