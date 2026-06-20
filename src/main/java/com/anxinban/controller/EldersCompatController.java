package com.anxinban.controller;

import com.anxinban.dto.ApiResponse;
import com.anxinban.dto.PageResult;
import com.anxinban.entity.ElderUser;
import com.anxinban.entity.EmergencyContact;
import com.anxinban.mapper.ElderUserRepository;
import com.anxinban.mapper.EmergencyContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 比赛兼容层 - 老人管理接口（复数路径 /api/elders）。
 *
 * <p>提供 snake_case 格式的老人列表，兼容前端 alerts.html / elderly.html。
 * 与原有 /api/elder/* 共存，不冲突。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/api/elders")
public class EldersCompatController {

    @Autowired
    private ElderUserRepository elderUserRepository;

    @Autowired
    private EmergencyContactRepository emergencyContactRepository;

    /**
     * 老人列表兼容接口。
     *
     * <p>支持分页与筛选（姓名、楼栋、房号、健康状态），返回 snake_case 格式字段。</p>
     *
     * @param page         页码，从 1 开始
     * @param pageSize     每页大小
     * @param name         按姓名模糊匹配（可选）
     * @param building     按楼栋精确匹配（可选）
     * @param room         按房号模糊匹配（可选）
     * @param healthStatus 按健康状态精确匹配：normal/warning/danger（可选）
     * @return 分页老人列表
     */
    @GetMapping
    public ApiResponse<PageResult<Map<String, Object>>> listElders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String building,
            @RequestParam(required = false) String room,
            @RequestParam(required = false) String healthStatus) {
        List<ElderUser> all = elderUserRepository.findAll();
        // 按 elder_id 排序
        all.sort(Comparator.comparing(ElderUser::getElderId));

        // 应用筛选条件
        List<ElderUser> filtered = all.stream()
                .filter(e -> name == null || name.isEmpty() || (e.getName() != null && e.getName().contains(name)))
                .filter(e -> building == null || building.isEmpty() || building.equals(e.getBuilding()))
                .filter(e -> room == null || room.isEmpty() || (e.getRoom() != null && e.getRoom().contains(room)))
                .filter(e -> healthStatus == null || healthStatus.isEmpty() || healthStatus.equals(e.getHealthStatus()))
                .toList();

        List<Map<String, Object>> list = new ArrayList<>();
        for (ElderUser e : filtered) {
            list.add(buildElderItem(e));
        }

        long total = list.size();
        int from = Math.max((page - 1) * pageSize, 0);
        int to = Math.min(from + pageSize, list.size());
        List<Map<String, Object>> paginated = from < list.size()
                ? new ArrayList<>(list.subList(from, to))
                : new ArrayList<>();

        PageResult<Map<String, Object>> result = new PageResult<>(paginated, total, page, pageSize);
        return ApiResponse.success(result);
    }

    /**
     * 构建单个老人数据项（snake_case 格式）。
     */
    private Map<String, Object> buildElderItem(ElderUser e) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("elder_id", e.getElderId());
        item.put("name", e.getName() != null ? e.getName() : "");
        item.put("age", e.getAge());
        item.put("gender", e.getGender() != null ? e.getGender() : "");
        item.put("building", e.getBuilding() != null ? e.getBuilding() : "");
        item.put("room", e.getRoom() != null ? e.getRoom() : "");
        // health_status 转换为中文
        item.put("status", mapHealthStatus(e.getHealthStatus()));
        item.put("phone", e.getPhone() != null ? e.getPhone() : "");

        // 查找首要紧急联系人
        List<EmergencyContact> contacts = emergencyContactRepository.findByElderIdOrderBySortOrderAsc(e.getElderId());
        EmergencyContact primary = null;
        if (contacts != null && !contacts.isEmpty()) {
            primary = contacts.get(0);
        }
        item.put("contact_name", primary != null ? primary.getName() : "");
        item.put("contact_phone", primary != null ? primary.getPhone() : (e.getFamilyPhone() != null ? e.getFamilyPhone() : ""));

        return item;
    }

    /**
     * 将 health_status 内部值映射为中文展示。
     */
    private String mapHealthStatus(String healthStatus) {
        if (healthStatus == null) return "正常";
        switch (healthStatus) {
            case "danger": return "高风险";
            case "warning": return "关注";
            default: return "正常";
        }
    }
}
