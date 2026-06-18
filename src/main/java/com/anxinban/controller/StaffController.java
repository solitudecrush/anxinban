package com.anxinban.controller;

/**
 * Staff REST 控制器，提供 Staff 相关的 HTTP API。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.StaffDto;
import com.anxinban.entity.StaffUser;
import com.anxinban.mapper.StaffUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    /** 字段含义待补充 */

    private final StaffUserRepository staffUserRepository;

    @Autowired
    public StaffController(StaffUserRepository staffUserRepository) {
        this.staffUserRepository = staffUserRepository;
    }

    /**
     * 处理 GET /list 请求。
     *
     * @param communityId communityId 参数
     * @return 处理结果
     */
    @GetMapping("/list")
    public ApiResponse<List<StaffDto>> listStaff(@RequestParam(required = false) String communityId) {
        List<StaffUser> entities;
        if (communityId != null && !communityId.isEmpty()) {
            entities = staffUserRepository.findByCommunityId(communityId);
        } else {
            entities = staffUserRepository.findAll();
        }
        List<StaffDto> dtos = entities.stream().map(this::convertToDto).collect(Collectors.toList());
        return ApiResponse.success(dtos);
    }

    /**
     * 新增接口，处理 POST  请求。
     *
     * @param dto dto 参数
     * @return 处理结果
     */
    @PostMapping
    public ApiResponse<StaffDto> createStaff(@RequestBody StaffDto dto) {
        StaffUser entity = new StaffUser();
        entity.setStaffId("STF-" + UUID.randomUUID().toString().substring(0, 8));
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setPassword("123456");
        entity.setRole(dto.getRole());
        entity.setCommunityId(dto.getCommunityId());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        StaffUser saved = staffUserRepository.save(entity);
        return ApiResponse.created(convertToDto(saved));
    }

    /**
     * 更新接口，处理 PUT /{staffId} 请求。
     *
     * @param staffId staffId 参数
     * @param dto dto 参数
     * @return 处理结果
     */
    @PutMapping("/{staffId}")
    public ApiResponse<StaffDto> updateStaff(@PathVariable String staffId, @RequestBody StaffDto dto) {
        StaffUser existing = staffUserRepository.findByStaffId(staffId);
        if (existing == null) {
            return ApiResponse.error(404, "工作人员不存在");
        }
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getPhone() != null) existing.setPhone(dto.getPhone());
        if (dto.getRole() != null) existing.setRole(dto.getRole());
        if (dto.getCommunityId() != null) existing.setCommunityId(dto.getCommunityId());
        existing.setUpdateTime(LocalDateTime.now());
        StaffUser saved = staffUserRepository.save(existing);
        return ApiResponse.success(convertToDto(saved));
    }

    /**
     * 删除接口，处理 DELETE /{staffId} 请求。
     *
     * @param staffId staffId 参数
     * @return 处理结果
     */
    @DeleteMapping("/{staffId}")
    public ApiResponse<Void> deleteStaff(@PathVariable String staffId) {
        StaffUser existing = staffUserRepository.findByStaffId(staffId);
        if (existing == null) {
            return ApiResponse.error(404, "工作人员不存在");
        }
        staffUserRepository.delete(existing);
        return ApiResponse.success(null);
    }

        /**
         * convertToDto 方法。
         *
         * @param entity 字段含义待补充
         */
    /**
     * 处理 请求  请求。
     *
     * @param entity entity 参数
     * @return 处理结果
     */
    private StaffDto convertToDto(StaffUser entity) {
        StaffDto dto = new StaffDto();
        dto.setStaffId(entity.getStaffId());
        dto.setUsername(entity.getUsername());
        dto.setName(entity.getName());
        dto.setPhone(entity.getPhone());
        dto.setRole(entity.getRole());
        dto.setCommunityId(entity.getCommunityId());
        dto.setAvatar(entity.getAvatar());
        return dto;
    }
}
