package com.anxinban.service;

/**
 * WorkOrder 业务服务类，处理 WorkOrder 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.PageResult;
import com.anxinban.dto.WorkOrderDto;
import com.anxinban.entity.WorkOrder;
import com.anxinban.mapper.ElderUserRepository;
import com.anxinban.mapper.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkOrderService {
    private final WorkOrderRepository workOrderRepository;
    /** 数据访问仓库，用于持久化操作 */
    private final ElderUserRepository elderUserRepository;

    @Autowired
    public WorkOrderService(WorkOrderRepository workOrderRepository, ElderUserRepository elderUserRepository) {
        this.workOrderRepository = workOrderRepository;
        this.elderUserRepository = elderUserRepository;
    }

        /**
         * createOrder 方法。
         *
         * @param dto 字段含义待补充
         */
    public WorkOrderDto createOrder(WorkOrderDto dto) {
        WorkOrder entity = convertToEntity(dto);
        // 如果前端未传 orderId，自动生成，修复数据库 NOT NULL 约束导致的 500 错误
        if (entity.getOrderId() == null || entity.getOrderId().isEmpty()) {
            entity.setOrderId("wo_" + java.util.UUID.randomUUID().toString().substring(0, 8));
        }
        entity.setStatus(dto.getStatus() == null ? "待分配" : dto.getStatus());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        // 未传 completeTime 时使用默认值，避免 NOT NULL 约束
        if (entity.getCompleteTime() == null) {
            entity.setCompleteTime(LocalDateTime.of(1970, 1, 1, 0, 0, 0));
        }
        WorkOrder saved = workOrderRepository.save(entity);
        return convertToDto(saved);
    }

        /**
         * 获取排序。
         *
         * @return 排序
         */
    public WorkOrderDto getOrder(String orderId) {
        WorkOrder entity = workOrderRepository.findByOrderId(orderId);
        return entity != null ? convertToDto(entity) : null;
    }

        /**
         * listOrders 方法。
         *
         * @param size 大小
         */
    public PageResult<WorkOrderDto> listOrders(String keyword, String elderName, String status, int page, int size) {
        List<WorkOrder> entities = workOrderRepository.findAll();
        List<WorkOrderDto> dtos = entities.stream()
                .map(this::convertToDto)
                .filter(o -> status == null || status.isEmpty() || status.equals(o.getStatus()))
                .filter(o -> keyword == null || keyword.isEmpty() || (o.getOrderId() != null && o.getOrderId().contains(keyword)))
                .filter(o -> elderName == null || elderName.isEmpty() || (o.getElderName() != null && o.getElderName().contains(elderName)))
                .collect(Collectors.toList());
        long total = dtos.size();
        List<WorkOrderDto> paginated = paginate(dtos, page, size);
        return new PageResult<>(paginated, total, page, size);
    }

        /**
         * listOrdersByElder 方法。
         *
         * @param elderId 关联老人用户 ID
         */
    public List<WorkOrderDto> listOrdersByElder(String elderId) {
        return workOrderRepository.findByElderIdOrderByCreatedAtDesc(elderId).stream()
                .map(this::convertToDto)
                .limit(10)
                .collect(Collectors.toList());
    }

        /**
         * updateOrderStatus 方法。
         *
         * @param status 状态标识
         */
    public WorkOrderDto updateOrderStatus(String orderId, String status) {
        WorkOrder existing = workOrderRepository.findByOrderId(orderId);
        if (existing == null) return null;
        existing.setStatus(status);
        if ("已完成".equals(status)) {
            existing.setCompleteTime(LocalDateTime.now());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        WorkOrder saved = workOrderRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * assignHandler 方法。
         *
         * @param handlerName 名称
         */
    public WorkOrderDto assignHandler(String orderId, String handlerId, String handlerName) {
        WorkOrder existing = workOrderRepository.findByOrderId(orderId);
        if (existing == null) return null;
        existing.setHandlerId(handlerId);
        existing.setHandlerName(handlerName);
        existing.setStatus("处理中");
        existing.setUpdatedAt(LocalDateTime.now());
        WorkOrder saved = workOrderRepository.save(existing);
        return convertToDto(saved);
    }

        /**
         * convertToDto 方法。
         *
         * @param entity 字段含义待补充
         */
    private WorkOrderDto convertToDto(WorkOrder entity) {
        WorkOrderDto dto = new WorkOrderDto();
        dto.setOrderId(entity.getOrderId());
        dto.setElderId(entity.getElderId());
        if (entity.getElderId() != null) {
            var elder = elderUserRepository.findByElderId(entity.getElderId());
            if (elder != null) {
                dto.setElderName(elder.getName());
            }
        }
        dto.setType(entity.getType());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setCreatorId(entity.getCreatorId());
        dto.setHandlerId(entity.getHandlerId());
        dto.setHandlerName(entity.getHandlerName());
        dto.setHandlerPhone(entity.getHandlerPhone());
        dto.setCreateTime(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
        dto.setCompleteTime(entity.getCompleteTime() != null ? entity.getCompleteTime().toString() : null);
        dto.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
        dto.setServiceRequestId(entity.getServiceRequestId());
        return dto;
    }

        /**
         * convertToEntity 方法。
         *
         * @param dto 字段含义待补充
         */
    private WorkOrder convertToEntity(WorkOrderDto dto) {
        WorkOrder entity = new WorkOrder();
        entity.setOrderId(dto.getOrderId());
        entity.setElderId(dto.getElderId());
        entity.setType(dto.getType());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setCreatorId(dto.getCreatorId());
        entity.setHandlerId(dto.getHandlerId());
        entity.setHandlerName(dto.getHandlerName());
        entity.setHandlerPhone(dto.getHandlerPhone());
        entity.setServiceRequestId(dto.getServiceRequestId());
        return entity;
    }

        /**
         * paginate 方法。
         *
         * @param size 大小
         */
    private List<WorkOrderDto> paginate(List<WorkOrderDto> items, int page, int size) {
        int from = Math.max((page - 1) * size, 0);
        int to = Math.min(from + size, items.size());
        if (from > items.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(items.subList(from, to));
    }
}
