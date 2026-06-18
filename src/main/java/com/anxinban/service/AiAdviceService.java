package com.anxinban.service;
import com.anxinban.entity.AiAdvice;
import com.anxinban.mapper.AiAdviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 建议服务。
 *
 * <p>负责 AI 健康/生活建议的持久化与查询，包括创建建议、按建议标识查询、
 * 按老人查询全部建议以及按老人和建议类型组合查询。</p>
 *
 * <p>事务边界：当前未显式开启事务，单次 Repository 保存由 Spring Data JPA
 * 隐式事务保证。</p>
 *
 * <p>依赖的 Mapper/Repository：</p>
 * <ul>
 *     <li>{@link AiAdviceRepository} — AI 建议记录的持久化与查询</li>
 * </ul>
 */
@Service
public class AiAdviceService {
    private final AiAdviceRepository aiAdviceRepository;

    /**
     * 构造器注入 AI 建议仓库。
     *
     * @param aiAdviceRepository AI 建议仓库
     */
    @Autowired
    public AiAdviceService(AiAdviceRepository aiAdviceRepository) {
        this.aiAdviceRepository = aiAdviceRepository;
    }

    /**
     * 创建一条 AI 建议。
     *
     * <p>业务规则：</p>
     * <ul>
     *     <li>adviceId 必须非空，否则抛出 {@link IllegalArgumentException}</li>
     *     <li>自动设置 createdAt 为当前时间</li>
     * </ul>
     *
     * @param advice 待保存的 AI 建议
     * @return 保存后的 AI 建议
     * @throws IllegalArgumentException 当 adviceId 为空时抛出
     */
    public AiAdvice createAdvice(AiAdvice advice) {
        // 业务规则校验：adviceId 为必填字段
        if (advice.getAdviceId() == null || advice.getAdviceId().isEmpty()) {
            throw new IllegalArgumentException("adviceId is required");
        }
        advice.setCreatedAt(LocalDateTime.now());
        return aiAdviceRepository.save(advice);
    }

    /**
     * 根据建议标识查询单条 AI 建议。
     *
     * @param adviceId 建议唯一标识
     * @return AI 建议；不存在时返回 {@code null}
     */
    public AiAdvice getAdvice(String adviceId) {
        return aiAdviceRepository.findByAdviceId(adviceId);
    }

    /**
     * 查询指定老人的全部 AI 建议。
     *
     * @param elderId 老人唯一标识
     * @return AI 建议列表，不会为 {@code null}
     */
    public List<AiAdvice> listByElder(String elderId) {
        return aiAdviceRepository.findByElderId(elderId);
    }

    /**
     * 按老人和建议类型组合查询 AI 建议。
     *
     * @param elderId    老人唯一标识
     * @param adviceType 建议类型
     * @return 符合条件的 AI 建议列表，不会为 {@code null}
     */
    public List<AiAdvice> listByElderAndType(String elderId, String adviceType) {
        return aiAdviceRepository.findByElderIdAndAdviceType(elderId, adviceType);
    }
}
