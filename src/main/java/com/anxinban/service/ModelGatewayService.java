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
            case "health_query":
                return "老人近日健康指标平稳，建议保持规律作息。";
            case "emergency.fall_help":
                return "已检测到紧急求助，正在联系家属和社区护理员。";
            case "home_control.light_on":
                return "已为您打开灯光。";
            default:
                return "收到您的请求，正在处理中。";
        }
    }
}
