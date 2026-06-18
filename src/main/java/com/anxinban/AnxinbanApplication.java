package com.anxinban;
import com.anxinban.mqtt.config.MqttProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 安心伴智慧家居后端应用主启动类。
 *
 * <p>该类作为整个 Spring Boot 应用的入口，负责初始化并启动 IoT 智慧养老/智能家居业务系统。
 * 本系统围绕“安心伴”主题，提供设备接入、事件告警、AI 对话、健康管理、工单处理等能力，
 * 后端通过 REST API 与前端/APP 交互，通过 MQTT 协议与家庭网关及硬件设备通信。</p>
 *
 * <p>核心功能模块包括：</p>
 * <ul>
 *   <li><b>MQTT 客户端服务</b>：连接 EMQX Broker，订阅设备主题、接收传感器/事件上报，并下发控制指令；</li>
 *   <li><b>设备模拟器</b>：在开发或演示阶段模拟 ESP32 传感器、摄像头、智能手表等硬件行为，循环生成测试数据；</li>
 *   <li><b>规则引擎</b>：根据设备事件联动处理，如夜间离床、陌生人闯入、烟雾告警、老人摔倒、紧急呼叫等；</li>
 *   <li><b>AI 智能体</b>：提供自然语言对话、物品查找、健康建议等智能化服务；</li>
 *   <li><b>REST API</b>：为管理后台、家属端、护工端提供数据查询、设备控制、告警处理等接口。</li>
 * </ul>
 */
@SpringBootApplication(exclude = {
        // 使用自定义 DataSourceConfig 显式构造数据源，排除自动配置避免驱动类推断失败
        org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration.class
})
@EnableConfigurationProperties(MqttProperties.class)
@EnableAsync
public class AnxinbanApplication {

    /**
     * 应用主入口方法。
     *
     * <p>使用 {@link SpringApplication#run(Class, String[])} 启动 Spring Boot 容器，
     * 完成组件扫描、自动配置、Bean 初始化、MQTT 客户端连接、模拟器启动等生命周期操作。</p>
     *
     * @param args 命令行传入的启动参数，可覆盖 application.properties 中的配置项，
     *             例如 {@code --mqtt.broker-host=192.168.1.100 --mqtt.simulator.enabled=true}
     */
    public static void main(String[] args) {
        SpringApplication.run(AnxinbanApplication.class, args);
    }

}
