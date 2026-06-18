package com.anxinban.mapper;


/**
 * CloudAgent 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.CloudAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 云端智能体数据访问接口。
 * <p>
 * 对应实体：{@link com.anxinban.entity.CloudAgent}。<br>
 * 主要职责：管理部署在云端的智能体实例信息，支持按智能体编号、运行状态、IP 地址等维度检索。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
@Repository
public interface CloudAgentRepository extends JpaRepository<CloudAgent, Long> {

    /**
     * 根据智能体编号查询单条云端智能体记录。
     *
     * @param agentId 智能体编号，业务唯一标识
     * @return 匹配的云端智能体记录；若不存在则返回 {@code null}
     */
    CloudAgent findByAgentId(String agentId);

    /**
     * 根据运行状态查询云端智能体列表。
     *
     * @param status 运行状态，例如在线、离线、维护中等
     * @return 该状态下的云端智能体列表；无记录时返回空列表
     */
    List<CloudAgent> findByStatus(String status);

    /**
     * 根据 IP 地址查询云端智能体列表。
     *
     * @param ip IP 地址
     * @return 绑定该 IP 的云端智能体列表；无记录时返回空列表
     */
    List<CloudAgent> findByIp(String ip);
}
