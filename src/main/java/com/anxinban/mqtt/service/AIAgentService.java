package com.anxinban.mqtt.service;
import com.anxinban.entity.AgentConversation;
import com.anxinban.mqtt.constant.MqttTopicConstants;
import com.anxinban.mqtt.dto.*;
import com.anxinban.mapper.AgentConversationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * AI 智能体服务。
 *
 * 作用：提供 AI 对话和物品查找功能，作为前端与大模型之间的桥梁。
 * 当前使用 Mock 实现返回预设意图，待后续替换为真实大模型 API。
 *
 * 功能：
 * 1. 智能体对话：接收用户文本，构造包含家庭状态的 Prompt，调用大模型
 * 2. AI 查找物品：接收用户请求，向指定摄像头发送抓图指令，调用多模态模型识别
 * 3. 异步处理：使用 @Async 避免阻塞主线程
 * 4. 对话日志：记录每次对话到数据库
 */
@Service
public class AIAgentService {

    private static final Logger log = LoggerFactory.getLogger(AIAgentService.class);
    private final MqttClientService mqttClientService;
    private final AgentConversationRepository conversationRepository;
    private final ObjectMapper objectMapper;
    private static final String DEFAULT_HOUSE_ID = "demo-house";

    /** Mock 意图映射表 */
    private static final Map<String, String> INTENT_MAP = new HashMap<>();

    static {
        // 灯光控制意图
        INTENT_MAP.put("打开灯", "light-control");
        INTENT_MAP.put("关灯", "light-control");
        INTENT_MAP.put("开灯", "light-control");
        INTENT_MAP.put("灯光", "light-control");
        
        // 窗帘控制意图
        INTENT_MAP.put("打开窗帘", "curtain-control");
        INTENT_MAP.put("关闭窗帘", "curtain-control");
        INTENT_MAP.put("窗帘", "curtain-control");
        
        // 查找物品意图
        INTENT_MAP.put("找", "find-item");
        INTENT_MAP.put("查找", "find-item");
        INTENT_MAP.put("在哪里", "find-item");
        
        // 健康查询意图
        INTENT_MAP.put("心率", "health-query");
        INTENT_MAP.put("血压", "health-query");
        INTENT_MAP.put("健康", "health-query");
        
        // 紧急呼叫意图
        INTENT_MAP.put("救命", "emergency");
        INTENT_MAP.put("求救", "emergency");
        INTENT_MAP.put("紧急", "emergency");
        
        // 闲聊意图
        INTENT_MAP.put("你好", "chat");
        INTENT_MAP.put("嗨", "chat");
        INTENT_MAP.put("在吗", "chat");
    }

    public AIAgentService(MqttClientService mqttClientService,
                          AgentConversationRepository conversationRepository,
                          ObjectMapper objectMapper) {
        this.mqttClientService = mqttClientService;
        this.conversationRepository = conversationRepository;
        this.objectMapper = objectMapper;
    }

    // ==================== 智能体对话 ====================
    @Async
    public CompletableFuture<AiConversationResp> sendMessageAsync(String userId, String content, String houseId) {
        log.info("收到对话请求：用户={}, 内容={}, 房屋={}", userId, content, houseId);

        try {
            // 构建请求
            String sessionId = UUID.randomUUID().toString();
            AiConversationReq request = new AiConversationReq(
                    UUID.randomUUID().toString(),
                    userId,
                    content,
                    sessionId
            );
            request.setDeviceStatusSnapshot(buildPrompt(content, houseId));

            // 记录对话日志
            saveConversation(userId, content, request.getSessionId(), "user");

            // 发布到 MQTT 主题
            String topic = MqttTopicConstants.agentRequest(houseId != null ? houseId : DEFAULT_HOUSE_ID);
            mqttClientService.publishJson(topic, request);

            // Mock 响应（模拟大模型返回）
            AiConversationResp response = mockResponse(request);

            // 记录响应日志
            saveConversation(userId, response.getReplyText(), request.getSessionId(), "agent");

            // 根据意图执行联动操作
            executeIntentAction(response, houseId != null ? houseId : DEFAULT_HOUSE_ID);

            return CompletableFuture.completedFuture(response);

        } catch (Exception e) {
            log.error("发送对话请求失败", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 构建包含家庭状态的 Prompt。
     */
    private String buildPrompt(String userInput, String houseId) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个智能家居助手，负责协助老人和家属管理家庭设备。\n");
        prompt.append("当前时间：").append(LocalDateTime.now()).append("\n");
        prompt.append("房屋ID：").append(houseId).append("\n");
        prompt.append("用户问题：").append(userInput).append("\n");
        prompt.append("请用友好、简洁的语言回答用户问题。");
        return prompt.toString();
    }

    /**
     * Mock 大模型响应。
     */
    private AiConversationResp mockResponse(AiConversationReq request) {
        AiConversationResp response = new AiConversationResp();
        response.setSessionId(request.getSessionId());
        response.setRequestMessageId(request.getMessageId());

        // 识别意图
        String intent = recognizeIntent(request.getText());
        response.setIntent(intent);

        // 根据意图生成响应
        switch (intent) {
            case "light-control":
                response.setReplyText("好的，我来帮您控制灯光。请问您想打开还是关闭哪个房间的灯？");
                break;
            case "curtain-control":
                response.setReplyText("好的，我来帮您控制窗帘。请问您想打开还是关闭窗帘？");
                break;
            case "find-item":
                response.setReplyText("好的，我来帮您查找物品。请告诉我您想找什么东西？");
                break;
            case "health-query":
                response.setReplyText("好的，我来帮您查询健康数据。");
                break;
            case "emergency":
                response.setReplyText("已收到紧急呼叫请求，正在通知紧急联系人并启动应急预案。");
                break;
            case "chat":
                response.setReplyText("您好！我是您的智能家居助手，请问有什么可以帮您的？");
                break;
            default:
                response.setReplyText("好的，我明白了。");
                break;
        }

        response.setTimestamp(java.time.Instant.now().toString());
        return response;
    }

    /**
     * 识别用户意图（简单实现）。
     */
    private String recognizeIntent(String content) {
        for (Map.Entry<String, String> entry : INTENT_MAP.entrySet()) {
            if (content.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "unknown";
    }

    /**
     * 根据意图执行联动操作。
     */
    private void executeIntentAction(AiConversationResp response, String houseId) throws MqttException {
        String intent = response.getIntent();

        switch (intent) {
            case "light-control":
                // 模拟灯光控制
                LightCmd cmd = new LightCmd("light-001", "toggle", "living-room");
                mqttClientService.publishJson(
                        MqttTopicConstants.actuatorCmd(houseId, "living-room", "light-001"),
                        cmd
                );
                log.info("已下发灯光控制指令");
                break;
            case "curtain-control":
                // 模拟窗帘控制
                CurtainCmd curtainCmd = new CurtainCmd("curtain-001", "toggle", "living-room");
                curtainCmd.setTargetPercent(50);
                mqttClientService.publishJson(
                        MqttTopicConstants.actuatorCmd(houseId, "living-room", "curtain-001"),
                        curtainCmd
                );
                log.info("已下发窗帘控制指令");
                break;
            case "emergency":
                // 紧急呼叫联动
                sendBuzzerCommand(houseId, "living-room", "beep", "紧急呼叫");
                log.info("紧急呼叫联动已执行");
                break;
            default:
                // 其他意图无需联动
        }
    }

    // ==================== AI 查找物品 ====================
    @Async
    public CompletableFuture<CameraFindItemResp> findItemAsync(String userId, String itemName, 
                                                                String room, String houseId) {
        log.info("收到查找物品请求：用户={}, 物品={}, 房间={}, 房屋={}", userId, itemName, room, houseId);

        try {
            String sessionId = UUID.randomUUID().toString();

            // 向目标摄像头发送抓图指令
            CameraFindItemReq req = new CameraFindItemReq(
                    "camera-" + room + "-001",
                    itemName,
                    room,
                    sessionId
            );
            req.setSource("voice");
            req.setUserId(userId);

            String topic = MqttTopicConstants.actuatorCmd(
                    houseId != null ? houseId : DEFAULT_HOUSE_ID,
                    room,
                    "camera-" + room + "-001"
            );
            mqttClientService.publishJson(topic, req);

            // Mock 多模态模型识别结果
            CameraFindItemResp resp = mockFindItemResponse(req);

            // 记录对话日志
            saveConversation(userId, "查找物品：" + itemName, sessionId, "user");
            saveConversation(userId, "查找结果：" + resp.getLocationDescription(), sessionId, "agent");

            return CompletableFuture.completedFuture(resp);

        } catch (Exception e) {
            log.error("查找物品请求失败", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Mock 查找物品响应。
     */
    private CameraFindItemResp mockFindItemResponse(CameraFindItemReq req) {
        CameraFindItemResp resp = new CameraFindItemResp(
                req.getDeviceId(),
                req.getSessionId(),
                req.getItemName(),
                req.getRoom()
        );

        // 随机决定是否找到
        boolean found = Math.random() > 0.2; // 80% 概率找到
        resp.setFound(found);
        resp.setStatus("completed");

        if (found) {
            resp.setImageUrl("file:///tmp/simulator/find_item_" + System.currentTimeMillis() + ".jpg");
            // 随机位置描述
            String[] locations = {"在茶几上", "在沙发旁边", "在书架上", "在电视柜上", "在餐桌上"};
            resp.setLocationDescription(req.getRoom() + locations[(int) (Math.random() * locations.length)]);
        } else {
            resp.setLocationDescription("未找到 '" + req.getItemName() + "'，请尝试其他房间");
        }

        return resp;
    }

    // ==================== 辅助方法 ====================

    /**
     * 记录对话日志到数据库。
     */
    private void saveConversation(String userId, String content, String sessionId, String role) {
        try {
            AgentConversation conversation = new AgentConversation();
            conversation.setConversationId(UUID.randomUUID().toString());
            conversation.setElderId(userId);
            conversation.setUserText(content);
            conversation.setConversationId(sessionId);
            conversation.setAgentType(role);
            conversation.setCreatedAt(LocalDateTime.now());
            conversationRepository.save(conversation);
        } catch (Exception e) {
            log.error("保存对话日志失败", e);
        }
    }

    /**
     * 发送蜂鸣器指令。
     */
    private void sendBuzzerCommand(String houseId, String room, String command, String reason) throws MqttException {
        BuzzerCmd cmd = new BuzzerCmd("buzzer-001", command, room);
        cmd.setReason(reason);
        cmd.setDurationMs(5000);
        cmd.setBeepCount(5);
        mqttClientService.publishJson(
                MqttTopicConstants.actuatorCmd(houseId, room, "buzzer-001"),
                cmd
        );
    }
}
