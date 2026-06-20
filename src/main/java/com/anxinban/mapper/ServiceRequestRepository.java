package com.anxinban.mapper;


/**
 * ServiceRequest 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    ServiceRequest findByRequestId(String requestId);
    List<ServiceRequest> findByFamilyId(String familyId);
    List<ServiceRequest> findByElderId(String elderId);
    List<ServiceRequest> findByStatus(String status);
    List<ServiceRequest> findByType(String requestType);
}
