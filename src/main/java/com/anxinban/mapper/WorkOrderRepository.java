package com.anxinban.mapper;


/**
 * WorkOrder 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    WorkOrder findByOrderId(String orderId);
    List<WorkOrder> findByElderId(String elderId);
    List<WorkOrder> findByStatus(String status);
    List<WorkOrder> findByHandlerId(String handlerId);
    List<WorkOrder> findByServiceRequestId(String serviceRequestId);
    List<WorkOrder> findByElderIdOrderByCreatedAtDesc(String elderId);

    /**
     * 统计指定时间之后创建的工单数量（用于本月工单统计）。
     *
     * @param since 起始时间
     * @return 工单数量
     */
    long countByCreatedAtAfter(LocalDateTime since);
}
