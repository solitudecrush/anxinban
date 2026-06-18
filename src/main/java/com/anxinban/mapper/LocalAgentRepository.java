package com.anxinban.mapper;


/**
 * LocalAgent 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.LocalAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalAgentRepository extends JpaRepository<LocalAgent, Long> {
    LocalAgent findByAgentId(String agentId);
    List<LocalAgent> findByStatus(String status);
    List<LocalAgent> findByIp(String ip);
}
