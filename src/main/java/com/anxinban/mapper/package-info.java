/**
 * 数据访问层（Repository/Mapper）包。
 * <p>
 * 本包基于 Spring Data JPA 定义各类实体对应的持久化访问接口，负责与关系型数据库进行交互，
 * 提供实体的增删改查、分页查询、统计计数以及按条件检索等能力。
 * 所有接口均继承自 {@link org.springframework.data.jpa.repository.JpaRepository}，
 * 并通过 Spring Data JPA 的方法派生查询机制实现常见的数据访问逻辑。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
package com.anxinban.mapper;
