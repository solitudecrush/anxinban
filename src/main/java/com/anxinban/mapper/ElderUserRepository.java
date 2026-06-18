package com.anxinban.mapper;


/**
 * ElderUser 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.ElderUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 老年人用户数据访问接口。
 * <p>
 * 对应实体：{@link com.anxinban.entity.ElderUser}。<br>
 * 主要职责：提供老年人基础信息档案的持久化访问，支持按老年人编号、手机号、姓名模糊、楼栋、健康状态以及社区编号等维度检索。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
@Repository
public interface ElderUserRepository extends JpaRepository<ElderUser, Long> {

    /**
     * 根据老年人编号查询单条老年人用户信息。
     *
     * @param elderId 老年人编号，业务唯一标识
     * @return 匹配的老年人用户记录；若不存在则返回 {@code null}
     */
    ElderUser findByElderId(String elderId);

    /**
     * 根据手机号查询老年人用户信息。
     *
     * @param phone 手机号
     * @return 匹配手机号的老年人用户记录；若不存在则返回 {@code null}
     */
    ElderUser findByPhone(String phone);

    /**
     * 根据姓名模糊查询老年人用户列表。
     * <p>
     * 查询逻辑：匹配姓名中包含指定关键字的老年人用户。
     * </p>
     *
     * @param name 姓名关键字
     * @return 姓名包含该关键字的老年人用户列表；无记录时返回空列表
     */
    List<ElderUser> findByNameContaining(String name);

    /**
     * 根据楼栋编号查询该楼栋下的老年人用户列表。
     *
     * @param building 楼栋编号
     * @return 该楼栋下的老年人用户列表；无记录时返回空列表
     */
    List<ElderUser> findByBuilding(String building);

    /**
     * 根据健康状态查询老年人用户列表。
     *
     * @param healthStatus 健康状态，例如健康、亚健康、需关注等
     * @return 该健康状态下的老年人用户列表；无记录时返回空列表
     */
    List<ElderUser> findByHealthStatus(String healthStatus);

    /**
     * 根据社区编号查询该社区下的老年人用户列表。
     *
     * @param communityId 社区编号
     * @return 该社区下的老年人用户列表；无记录时返回空列表
     */
    List<ElderUser> findByCommunityId(String communityId);
}
