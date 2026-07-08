package com.anxinban.service;

/**
 * ModelGateway 业务服务类，处理 ModelGateway 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import org.springframework.stereotype.Service;

@Service
public class ModelGatewayService {

        /**
         * 判断是否字段含义待补充。
         *
         * @return 是否字段含义待补充
         */
    public boolean isHealthy() {
        return true;
    }

        /**
         * mockResponse 方法。
         *
         * @param prompt 字段含义待补充
         */
    public String mockResponse(String prompt) {
        return "【Mock】基于规则生成的回复：" + prompt;
    }

        /**
         * callThirdPartyLLM 方法。
         *
         * @param prompt 字段含义待补充
         */
    public String callThirdPartyLLM(String prompt) {
        return mockResponse(prompt);
    }

        /**
         * callLocalFineTunedModel 方法。
         *
         * @param prompt 字段含义待补充
         */
    public String callLocalFineTunedModel(String prompt) {
        return mockResponse(prompt);
    }

        /**
         * ruleBasedResponse 方法。
         *
         * @param intent 意图
         */
    public String ruleBasedResponse(String intent) {
        switch (intent) {
            case "find_item":
                return "正在帮您查找物品，请稍等。";
            case "control_music":
                return "已为您播放音乐，祝您心情愉快。";
            case "companion_chat":
                return "我在呢，您慢慢说。我一直在这儿陪着您。";
            default:
                return "收到您的消息，我正在为您处理。";
        }
    }
}
