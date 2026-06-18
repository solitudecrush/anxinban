package com.anxinban.mapper;


/**
 * EmergencyContact 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 紧急联系人数据访问接口。
 * <p>
 * 对应实体：{@link com.anxinban.entity.EmergencyContact}。<br>
 * 主要职责：管理老年人的紧急联系人信息，支持按联系人编号、老年人编号检索，并可按优先级升序返回联系列表。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {

    /**
     * 根据联系人编号查询单条紧急联系人记录。
     *
     * @param contactId 联系人编号，业务唯一标识
     * @return 匹配的紧急联系人记录；若不存在则返回 {@code null}
     */
    EmergencyContact findByContactId(String contactId);

    /**
     * 根据老年人编号查询其所有紧急联系人。
     *
     * @param elderId 老年人编号
     * @return 该老年人的紧急联系人列表；无记录时返回空列表
     */
    List<EmergencyContact> findByElderId(String elderId);

    /**
     * 根据老年人编号查询其紧急联系人，并按排序值升序排列。
     * <p>
     * 查询逻辑：先按老年人编号过滤，再根据 {@code sortOrder} 字段从小到大排序。
     * </p>
     *
     * @param elderId 老年人编号
     * @return 按优先级升序排列的紧急联系人列表；无记录时返回空列表
     */
    List<EmergencyContact> findByElderIdOrderBySortOrderAsc(String elderId);
}
