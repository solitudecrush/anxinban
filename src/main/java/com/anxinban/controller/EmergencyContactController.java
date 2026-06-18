package com.anxinban.controller;

/**
 * EmergencyContact REST 控制器，提供 EmergencyContact 相关的 HTTP API。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.EmergencyContactDto;
import com.anxinban.service.EmergencyContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergency-contact")
public class EmergencyContactController {
    /** 是否为紧急情况 */

    private final EmergencyContactService emergencyContactService;

    @Autowired
    public EmergencyContactController(EmergencyContactService emergencyContactService) {
        this.emergencyContactService = emergencyContactService;
    }

    /**
     * 新增接口，处理 POST  请求。
     *
     * @param contact contact 参数
     * @return 处理结果
     */
    @PostMapping
    public ApiResponse<EmergencyContactDto> createContact(@RequestBody EmergencyContactDto contact) {
        return ApiResponse.created(emergencyContactService.createContact(contact));
    }

    /**
     * 查询接口，处理 GET /{contactId} 请求。
     *
     * @param contactId contactId 参数
     * @return 处理结果
     */
    @GetMapping("/{contactId}")
    public ApiResponse<EmergencyContactDto> getContact(@PathVariable String contactId) {
        EmergencyContactDto contact = emergencyContactService.getContact(contactId);
        if (contact == null) {
            return ApiResponse.error(404, "联系人不存在");
        }
        return ApiResponse.success(contact);
    }

    /**
     * 处理 GET /list 请求。
     *
     * @param elderId elderId 参数
     * @return 处理结果
     */
    @GetMapping("/list")
    public ApiResponse<List<EmergencyContactDto>> listContacts(@RequestParam String elderId) {
        return ApiResponse.success(emergencyContactService.listContactsByElder(elderId));
    }

    /**
     * 更新接口，处理 PUT /{contactId} 请求。
     *
     * @param contactId contactId 参数
     * @param contact contact 参数
     * @return 处理结果
     */
    @PutMapping("/{contactId}")
    public ApiResponse<EmergencyContactDto> updateContact(@PathVariable String contactId, @RequestBody EmergencyContactDto contact) {
        EmergencyContactDto updated = emergencyContactService.updateContact(contactId, contact);
        if (updated == null) {
            return ApiResponse.error(404, "联系人不存在");
        }
        return ApiResponse.success(updated);
    }

    /**
     * 删除接口，处理 DELETE /{contactId} 请求。
     *
     * @param contactId contactId 参数
     * @return 处理结果
     */
    @DeleteMapping("/{contactId}")
    public ApiResponse<Void> deleteContact(@PathVariable String contactId) {
        boolean removed = emergencyContactService.deleteContact(contactId);
        if (!removed) {
            return ApiResponse.error(404, "联系人不存在");
        }
        return ApiResponse.success(null);
    }
}
