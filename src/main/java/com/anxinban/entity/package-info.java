/**
 * 安心伴智慧家居系统 JPA 实体层。
 *
 * <p>本包定义了与 MySQL 数据库表一一对应的实体类，使用 Jakarta Persistence API（JPA）
 * 进行对象关系映射（ORM）。每个实体类对应一个业务领域对象，包含字段定义、关联关系
 * 及生命周期回调方法。</p>
 *
 * <p>实体层设计原则：</p>
 * <ul>
 *   <li>实体类只负责数据承载，不包含复杂业务逻辑</li>
 *   <li>主键统一使用 {@link jakarta.persistence.GeneratedValue} 自增策略</li>
 *   <li>关联关系使用懒加载（FetchType.LAZY）避免 N+1 查询问题</li>
 *   <li>时间字段统一使用 {@link java.time.LocalDateTime}</li>
 * </ul>
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
package com.anxinban.entity;
