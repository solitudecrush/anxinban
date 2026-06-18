package com.anxinban.mapper;


/**
 * MonitorRequest 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.MonitorRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MonitorRequestRepository extends JpaRepository<MonitorRequest, Long> {
    MonitorRequest findByRequestId(String requestId);
    List<MonitorRequest> findByElderId(String elderId);
    List<MonitorRequest> findByStaffId(String staffId);
    List<MonitorRequest> findByElderIdAndStatus(String elderId, String status);
    List<MonitorRequest> findByElderIdAndStatusAndExpiredAtAfter(String elderId, String status, LocalDateTime now);
}
