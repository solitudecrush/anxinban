package com.anxinban.service;

/**
 * HomeControlLog 业务服务类，处理 HomeControlLog 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.entity.HomeControlLog;
import com.anxinban.mapper.HomeControlLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HomeControlLogService {
    /** 字段含义待补充 */

    private final HomeControlLogRepository homeControlLogRepository;

    @Autowired
    public HomeControlLogService(HomeControlLogRepository homeControlLogRepository) {
        this.homeControlLogRepository = homeControlLogRepository;
    }

        /**
         * saveControlLog 方法。
         *
         * @param log 字段含义待补充
         */
    public HomeControlLog saveControlLog(HomeControlLog log) {
        if (log.getControlId() == null || log.getControlId().isEmpty()) {
            throw new IllegalArgumentException("controlId is required");
        }
        log.setCreatedAt(LocalDateTime.now());
        return homeControlLogRepository.save(log);
    }

        /**
         * 获取字段含义待补充。
         *
         * @return 字段含义待补充
         */
    public HomeControlLog getControlLog(String controlId) {
        return homeControlLogRepository.findByControlId(controlId);
    }

        /**
         * listByElder 方法。
         *
         * @param elderId 关联老人用户 ID
         */
    public List<HomeControlLog> listByElder(String elderId) {
        return homeControlLogRepository.findByElderId(elderId);
    }
}
