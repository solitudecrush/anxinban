package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.entity.FamilyUser;
import com.anxinban.entity.StaffUser;
import com.anxinban.mapper.FamilyUserRepository;
import com.anxinban.mapper.StaffUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器。
 *
 * <p>提供头像、快照等文件上传接口。上传的文件存储到服务器本地目录，
 * 通过静态资源映射对外提供 HTTP 访问。</p>
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${app.upload.dir:uploads}")
    private String uploadRoot;

    private final FamilyUserRepository familyUserRepository;
    private final StaffUserRepository staffUserRepository;

    @Autowired
    public FileUploadController(FamilyUserRepository familyUserRepository,
                                StaffUserRepository staffUserRepository) {
        this.familyUserRepository = familyUserRepository;
        this.staffUserRepository = staffUserRepository;
    }

    /**
     * 头像上传接口。
     *
     * <p>接收 multipart/form-data，保存到 {@code uploads/avatars/} 目录，
     * 更新对应用户的 avatar 字段，返回可访问的头像 URL。</p>
     *
     * @param file   上传的图片文件（multipart/form-data，字段名 "file"）
     * @param userId 用户 ID（staffId 或 familyId）
     * @param role   用户角色（"staff" 或 "family"），用于确定查哪张表
     * @return 包含头像 URL 的响应
     */
    @PostMapping("/avatar")
    public ApiResponse<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam(value = "role", defaultValue = "family") String role) {

        // 1. 校验文件
        if (file.isEmpty()) {
            return ApiResponse.error(400, "文件不能为空");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ApiResponse.error(400, "只支持上传图片文件");
        }

        // 2. 生成文件名（保留原始扩展名）
        String originalName = file.getOriginalFilename();
        String ext = ".jpg";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String filename = "avatar-" + UUID.randomUUID().toString().substring(0, 8) + ext;

        // 3. 确保上传目录存在
        try {
            Path avatarDir = Paths.get(uploadRoot, "avatars");
            Files.createDirectories(avatarDir);

            // 4. 保存文件
            Path targetPath = avatarDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 5. 构建相对 URL
            String avatarUrl = "/uploads/avatars/" + filename;

            // 6. 更新用户记录的 avatar 字段
            boolean updated = updateUserAvatar(userId, role, avatarUrl);
            if (!updated) {
                // 用户不存在，删除已上传的文件
                Files.deleteIfExists(targetPath);
                return ApiResponse.error(404, "用户不存在");
            }

            return ApiResponse.success(Map.of("url", avatarUrl));
        } catch (IOException e) {
            return ApiResponse.error(500, "文件保存失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户头像（恢复默认头像）。
     */
    @DeleteMapping("/avatar")
    public ApiResponse<Map<String, String>> deleteAvatar(
            @RequestParam("userId") String userId,
            @RequestParam(value = "role", defaultValue = "family") String role) {

        boolean updated = updateUserAvatar(userId, role, null);
        if (!updated) {
            return ApiResponse.error(404, "用户不存在");
        }
        return ApiResponse.success(Map.of("url", "/uploads/avatars/default.png"));
    }

    /**
     * 更新用户的 avatar 字段。
     *
     * @param userId 用户 ID
     * @param role   角色（staff 或 family）
     * @param avatarUrl 头像 URL（传 null 表示恢复默认）
     * @return 是否找到并更新了用户
     */
    private boolean updateUserAvatar(String userId, String role, String avatarUrl) {
        if ("staff".equals(role)) {
            StaffUser staff = staffUserRepository.findByStaffId(userId);
            if (staff != null) {
                staff.setAvatar(avatarUrl);
                staff.setUpdatedAt(LocalDateTime.now());
                staffUserRepository.save(staff);
                return true;
            }
        } else {
            FamilyUser family = familyUserRepository.findByFamilyId(userId);
            if (family != null) {
                family.setAvatar(avatarUrl);
                family.setUpdatedAt(LocalDateTime.now());
                familyUserRepository.save(family);
                return true;
            }
        }
        return false;
    }

    /**
     * 快照上传接口（监控截图等）。
     */
    @PostMapping("/snapshot")
    public ApiResponse<Map<String, String>> uploadSnapshot(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.error(400, "文件不能为空");
        }
        try {
            Path snapshotDir = Paths.get(uploadRoot, "snapshots");
            Files.createDirectories(snapshotDir);

            String filename = "snapshot-" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
            Path targetPath = snapshotDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String url = "/uploads/snapshots/" + filename;
            return ApiResponse.success(Map.of("url", url));
        } catch (IOException e) {
            return ApiResponse.error(500, "快照保存失败: " + e.getMessage());
        }
    }
}
