package com.anxinban.mapper;


/**
 * HomeControlLog 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.HomeControlLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 家居控制日志数据访问接口。
 * <p>
 * 对应实体：{@link com.anxinban.entity.HomeControlLog}。<br>
 * 主要职责：记录并检索智能家居控制指令的执行日志，支持按控制编号、老年人编号、设备编号、
 * 来源智能体以及执行结果等维度查询。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
@Repository
public interface HomeControlLogRepository extends JpaRepository<HomeControlLog, Long> {

    /**
     * 根据控制编号查询单条家居控制日志。
     *
     * @param controlId 控制编号，业务唯一标识
     * @return 匹配的家居控制日志；若不存在则返回 {@code null}
     */
    HomeControlLog findByControlId(String controlId);

    /**
     * 根据老年人编号查询其关联的所有家居控制日志。
     *
     * @param elderId 老年人编号
     * @return 该老年人的家居控制日志列表；无记录时返回空列表
     */
    List<HomeControlLog> findByElderId(String elderId);

    /**
     * 根据设备编号查询该设备的家居控制日志。
     *
     * @param deviceId 设备编号
     * @return 该设备的控制日志列表；无记录时返回空列表
     */
    List<HomeControlLog> findByDeviceId(String deviceId);

    /**
     * 根据来源智能体查询由其触发的家居控制日志。
     *
     * @param sourceAgent 来源智能体标识
     * @return 该智能体触发的控制日志列表；无记录时返回空列表
     */
    List<HomeControlLog> findBySourceAgent(String sourceAgent);

    /**
     * 根据执行结果查询家居控制日志。
     *
     * @param result 执行结果，例如成功、失败、超时等
     * @return 该执行结果下的控制日志列表；无记录时返回空列表
     */
    List<HomeControlLog> findByResult(String result);
}
