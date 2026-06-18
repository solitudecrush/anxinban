package com.anxinban.service;

/**
 * Sensor 业务服务类，处理 Sensor 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import org.springframework.stereotype.Service;

@Service
public class SensorService {

        /**
         * validateSensorPayload 方法。
         *
         * @param payload 消息负载/原始数据
         */
    public boolean validateSensorPayload(Object payload) {
        return payload != null;
    }
}
