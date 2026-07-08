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

    /** 意图映射表 — 将用户输入关键词映射到三大意图分类 */
    private static final Map<String, String> INTENT_MAP = new HashMap<>();

    static {
        // ========== 找东西 (find_item) ==========
        INTENT_MAP.put("找", "find_item");
        INTENT_MAP.put("查找", "find_item");
        INTENT_MAP.put("在哪里", "find_item");
        INTENT_MAP.put("找不到", "find_item");
        INTENT_MAP.put("眼镜", "find_item");
        INTENT_MAP.put("钥匙", "find_item");

        // ========== 音乐 (control_music) ==========
        INTENT_MAP.put("音乐", "control_music");
        INTENT_MAP.put("歌", "control_music");
        INTENT_MAP.put("播放", "control_music");
        INTENT_MAP.put("听", "control_music");
        INTENT_MAP.put("戏曲", "control_music");
        INTENT_MAP.put("收音机", "control_music");

        // ========== 陪伴互动 (companion_chat) — 其余所有日常对话 ==========
        // 健康相关
        INTENT_MAP.put("心率", "companion_chat");
        INTENT_MAP.put("血压", "companion_chat");
        INTENT_MAP.put("健康", "companion_chat");
        INTENT_MAP.put("不舒服", "companion_chat");
        INTENT_MAP.put("疼", "companion_chat");
        // 家居控制（通过对话方式）
        INTENT_MAP.put("开灯", "companion_chat");
        INTENT_MAP.put("关灯", "companion_chat");
        INTENT_MAP.put("灯光", "companion_chat");
        INTENT_MAP.put("窗帘", "companion_chat");
        // 闲聊
        INTENT_MAP.put("你好", "companion_chat");
        INTENT_MAP.put("嗨", "companion_chat");
        INTENT_MAP.put("在吗", "companion_chat");
        INTENT_MAP.put("天气", "companion_chat");
        INTENT_MAP.put("心情", "companion_chat");
        INTENT_MAP.put("孤单", "companion_chat");
        INTENT_MAP.put("想", "companion_chat");
        INTENT_MAP.put("开心", "companion_chat");
        INTENT_MAP.put("睡", "companion_chat");
        // 注意：不再包含 emergency 关键词 — 紧急呼救通过手表按键触发，不经过语音对话
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
            executeIntentAction(response, request.getText(), houseId != null ? houseId : DEFAULT_HOUSE_ID);

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
            case "find_item":
                response.setReplyText("好的，我来帮您找。请告诉我您想找什么物品？我帮您启动摄像头查找。");
                break;
            case "control_music":
                response.setReplyText("好的，正在为您播放音乐。如果想换一首或者调节音量，随时告诉我。");
                break;
            case "companion_chat":
                response.setReplyText(generateCompanionReply(request.getText()));
                break;
            default:
                response.setReplyText("我在呢，您慢慢说。有什么我可以帮您的？");
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
     * 根据用户输入文本中的关键词执行联动操作。
     * 注：intent 已简化为三大类，具体的设备控制通过原始文本关键词判断。
     */
    private void executeIntentAction(AiConversationResp response, String userText, String houseId) throws MqttException {
        // 灯光控制：检测文本中包含灯相关关键词
        if (userText.contains("灯")) {
            LightCmd cmd = new LightCmd("light-001", "toggle", "living-room");
            mqttClientService.publishJson(
                    MqttTopicConstants.actuatorCmd(houseId, "living-room", "light-001"),
                    cmd
            );
            log.info("已下发灯光控制指令（用户输入: {}）", userText);
        }

        // 窗帘控制：检测文本中包含窗帘关键词
        if (userText.contains("窗帘")) {
            CurtainCmd curtainCmd = new CurtainCmd("curtain-001", "toggle", "living-room");
            curtainCmd.setTargetPercent(50);
            mqttClientService.publishJson(
                    MqttTopicConstants.actuatorCmd(houseId, "living-room", "curtain-001"),
                    curtainCmd
            );
            log.info("已下发窗帘控制指令（用户输入: {}）", userText);
        }

        // 注意：不再处理 emergency 意图 — 紧急呼救通过手表硬件按键触发，不走语音对话通道
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
     * 生成陪伴互动的温暖回复。
     */
    private String generateCompanionReply(String userText) {
        if (userText == null) return "我在呢，有什么可以帮您的？";

        if (userText.contains("心情") || userText.contains("不开心") || userText.contains("难过")) {
            return "我理解您的心情。有时候说出来就会好很多，我一直在这儿陪着您呢。要不要我给您放首喜欢的歌，或者帮您给家人打个电话？";
        }
        if (userText.contains("孤单") || userText.contains("寂寞")) {
            return "您不孤单，我一直都在您身边。您的家人们也都惦记着您呢。要不我陪您聊聊天，讲讲今天的新闻？";
        }
        if (userText.contains("想") && (userText.contains("孙子") || userText.contains("儿子") || userText.contains("女儿") || userText.contains("家人"))) {
            return "亲情是最温暖的牵挂。要不要我现在帮您拨个视频电话给他们？看到他们的笑脸您一定会开心的。";
        }
        if (userText.contains("开心") || userText.contains("高兴") || userText.contains("好吃")) {
            return "真好！开心的时候就要好好享受。您把开心的心情保持下去，身体也会越来越好的。";
        }
        if (userText.contains("疼") || userText.contains("不舒服")) {
            return "您哪里不舒服要告诉我，别忍着。我先帮您联系医护人员，同时您深呼吸放松一下，我在这儿陪着您。";
        }
        if (userText.contains("睡") && (userText.contains("不好") || userText.contains("失眠"))) {
            return "睡不好确实让人心烦。我给您放一段舒缓的音乐，您躺下来慢慢放松。睡前别想太多，我在这儿守着您。";
        }
        if (userText.contains("血压") || userText.contains("心率") || userText.contains("健康")) {
            return "您的健康数据我已经帮您查看了。记得按时吃药、保持心情愉快，有什么不舒服随时告诉我。";
        }
        if (userText.contains("天气") || userText.contains("出去")) {
            return "今天天气不错呢！晒晒太阳、散散步对身体特别好。出门记得带好拐杖，注意安全，别走太远哦。";
        }
        if (userText.contains("灯")) {
            return "好的，我来帮您控制灯光。已经为您操作好了。还有其他需要我帮忙的吗？";
        }
        if (userText.contains("窗帘")) {
            return "好的，我来帮您控制窗帘。已经为您调整好了。还有其他需要我帮忙的吗？";
        }
        // 默认陪伴回复
        return "我在呢，您说吧。不管什么事儿，我都在这儿陪着您。";
    }

    /**
     * 记录对话日志到数据库。
     */
    private void saveConversation(String userId, String content, String sessionId, String role) {
        try {
            AgentConversation conversation = new AgentConversation();
            conversation.setConversationId(sessionId + "_" + role);
            conversation.setElderId(userId);
            conversation.setUserText(content);
            conversation.setAgentType(role);
            conversation.setRiskLevel("low");
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
