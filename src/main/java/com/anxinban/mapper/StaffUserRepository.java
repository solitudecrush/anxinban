package com.anxinban.mapper;


/**
 * StaffUser 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffUserRepository extends JpaRepository<StaffUser, Long> {
    StaffUser findByStaffId(String staffId);
    StaffUser findByPhone(String phone);
    List<StaffUser> findByCommunityId(String communityId);
    List<StaffUser> findByNameContaining(String name);
}
