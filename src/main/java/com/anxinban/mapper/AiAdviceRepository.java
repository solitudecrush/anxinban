package com.anxinban.mapper;


/**
 * AiAdvice 数据访问接口，基于 Spring Data JPA 实现持久化操作。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import com.anxinban.entity.AiAdvice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI 建议数据访问接口。
 * <p>
 * 对应实体：{@link com.anxinban.entity.AiAdvice}。<br>
 * 主要职责：管理由 AI 生成的健康/生活建议数据，支持按建议编号、老年人编号、建议类型等条件检索。
 * </p>
 *
 * @author anxinban
 * @since 1.0
 */
@Repository
public interface AiAdviceRepository extends JpaRepository<AiAdvice, Long> {

    /**
     * 根据建议编号查询单条 AI 建议。
     *
     * @param adviceId 建议编号，业务唯一标识
     * @return 匹配的 AI 建议记录；若不存在则返回 {@code null}
     */
    AiAdvice findByAdviceId(String adviceId);

    /**
     * 根据老年人编号查询其收到的全部 AI 建议。
     *
     * @param elderId 老年人编号
     * @return 该老年人的 AI 建议列表；无记录时返回空列表
     */
    List<AiAdvice> findByElderId(String elderId);

    /**
     * 根据建议类型查询 AI 建议。
     *
     * @param adviceType 建议类型，例如健康提醒、用药提醒等
     * @return 该类型下的 AI 建议列表；无记录时返回空列表
     */
    List<AiAdvice> findByAdviceType(String adviceType);

    /**
     * 根据老年人编号与建议类型联合查询 AI 建议。
     *
     * @param elderId    老年人编号
     * @param adviceType 建议类型
     * @return 同时满足老年人编号与建议类型的 AI 建议列表；无记录时返回空列表
     */
    List<AiAdvice> findByElderIdAndAdviceType(String elderId, String adviceType);
}
