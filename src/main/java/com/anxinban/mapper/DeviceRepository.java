package com.anxinban.mapper;


/**
 * Device 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 设备数据访问接口。
 * <p>
 * 对应实体：{@link com.anxinban.entity.Device}。<br>
 * 主要职责：提供 IoT 设备信息的持久化访问，支持按设备编号、所属老年人、设备状态、设备类型以及安装位置等维度检索。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    /**
     * 根据设备编号查询单条设备记录。
     *
     * @param deviceId 设备编号，业务唯一标识
     * @return 匹配的设备记录；若不存在则返回 {@code null}
     */
    Device findByDeviceId(String deviceId);

    /**
     * 根据老年人编号查询其绑定的所有设备。
     *
     * @param elderId 老年人编号
     * @return 该老年人绑定的设备列表；无记录时返回空列表
     */
    List<Device> findByElderId(String elderId);

    /**
     * 根据设备状态查询设备列表。
     *
     * @param status 设备状态，例如在线、离线、故障等
     * @return 该状态下的设备列表；无记录时返回空列表
     */
    List<Device> findByStatus(String status);

    /**
     * 根据设备类型查询设备列表。
     *
     * @param deviceType 设备类型，例如血压计、摄像头、门磁等
     * @return 该类型的设备列表；无记录时返回空列表
     */
    List<Device> findByType(String deviceType);

    /**
     * 根据设备安装位置查询设备列表。
     *
     * @param location 安装位置，例如客厅、卧室、卫生间等
     * @return 该位置的设备列表；无记录时返回空列表
     */
    List<Device> findByLocation(String location);
}
