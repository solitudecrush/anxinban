/**
 * 安心伴智慧家居系统业务服务层。
 *
 * <p>本包负责处理具体的业务逻辑，是控制器层与数据访问层之间的桥梁。
 * 服务类通过调用 Mapper/Repository 完成数据持久化，并进行业务规则校验、
 * 数据转换、事务控制及跨模块协作。</p>
 *
 * <p>服务层设计原则：</p>
 * <ul>
 *   <li>每个服务类对应一个明确的业务领域</li>
 *   <li>复杂业务操作使用 {@link org.springframework.transaction.annotation.Transactional} 保证事务一致性</li>
 *   <li>Service 层不直接处理 HTTP 请求响应，只接收 DTO/参数并返回 DTO/实体</li>
 *   <li>核心业务异常通过抛出 RuntimeException 或自定义异常向上传递</li>
 * </ul>
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
package com.anxinban.service;
