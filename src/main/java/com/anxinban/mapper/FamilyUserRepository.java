package com.anxinban.mapper;


/**
 * FamilyUser 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.FamilyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 家属用户数据访问接口。
 * <p>
 * 对应实体：{@link com.anxinban.entity.FamilyUser}。<br>
 * 主要职责：管理老年人家属用户账号信息，支持按家属编号、手机号以及所关联老年人编号检索。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
@Repository
public interface FamilyUserRepository extends JpaRepository<FamilyUser, Long> {

    /**
     * 根据家属编号查询单条家属用户记录。
     *
     * @param familyId 家属编号，业务唯一标识
     * @return 匹配的家属用户记录；若不存在则返回 {@code null}
     */
    FamilyUser findByFamilyId(String familyId);

    /**
     * 根据手机号查询家属用户记录。
     *
     * @param phone 手机号
     * @return 匹配手机号的家属用户记录；若不存在则返回 {@code null}
     */
    FamilyUser findByPhone(String phone);

    /**
     * 根据所关联的老年人编号查询家属用户记录。
     *
     * @param elderId 老年人编号
     * @return 关联该老年人的家属用户记录；若不存在则返回 {@code null}
     */
    FamilyUser findByElderId(String elderId);
}
