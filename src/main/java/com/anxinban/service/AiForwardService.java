package com.anxinban.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * AI 转发服务 — 将 AI 接口请求转发到外部 Python AI 服务。
 *
 * <p>如果 AI_SERVICE_BASE_URL 未配置、转发被禁用、或外部 AI 服务不可达，
 * 本服务返回 null，由调用方回退到 Java 本地规则/Mock 逻辑。</p>
 *
 * <p>目标地址映射（path 直接拼接）：</p>
 * <pre>
 *   POST /api/ai/health-analysis → ${AI_SERVICE_BASE_URL}/api/ai/health-analysis
 *   POST /api/ai/chat/quick      → ${AI_SERVICE_BASE_URL}/api/ai/chat/quick
 *   POST /api/ai/chat/deep       → ${AI_SERVICE_BASE_URL}/api/ai/chat/deep
 *   POST /api/ai/rag-query       → ${AI_SERVICE_BASE_URL}/api/ai/rag-query
 *   POST /api/ai/vision-analysis → ${AI_SERVICE_BASE_URL}/api/ai/vision-analysis
 * </pre>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class AiForwardService {

    private static final Logger log = LoggerFactory.getLogger(AiForwardService.class);

    @Value("${ai.service.base-url:}")
    private String aiServiceBaseUrl;

    @Value("${ai.service.timeout-seconds:15}")
    private int timeoutSeconds;

    @Value("${ai.service.enable-forward:true}")
    private boolean enableForward;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private HttpClient httpClient;

    @PostConstruct
    public void init() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();
        log.info("AiForwardService initialized — baseUrl=\"{}\", timeout={}s, forward={}",
                aiServiceBaseUrl, timeoutSeconds, enableForward);
    }

    // ==================== 公开 API ====================

    /**
     * 判断是否启用了外部 AI 转发。
     *
     * @return true 如果 base-url 非空且 enable-forward 为 true
     */
    public boolean isForwardEnabled() {
        return enableForward && aiServiceBaseUrl != null && !aiServiceBaseUrl.isBlank();
    }

    /**
     * 获取当前配置的 AI 服务地址。
     *
     * @return base-url 字符串
     */
    public String getAiServiceBaseUrl() {
        return aiServiceBaseUrl;
    }

    /**
     * 检测 Python AI 服务是否可达。
     * <p>首先尝试 GET ${base-url}/api/health，失败则尝试 GET ${base-url}/。</p>
     *
     * @return true 如果至少一个端点返回 200
     */
    public boolean isPythonAiReachable() {
        if (!isForwardEnabled()) {
            return false;
        }
        // 尝试 /api/health
        if (healthCheck(aiServiceBaseUrl + "/api/health")) {
            return true;
        }
        // 回退到根路径
        if (healthCheck(aiServiceBaseUrl + "/")) {
            return true;
        }
        log.debug("Python AI service not reachable at {}", aiServiceBaseUrl);
        return false;
    }

    /**
     * 转发 POST 请求到外部 Python AI 服务。
     *
     * @param path        请求路径（如 /api/ai/health-analysis）
     * @param requestBody 请求体对象，将被序列化为 JSON
     * @return Python AI 返回的 JSON Map；如果转发失败返回 null
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> forward(String path, Object requestBody) {
        if (!isForwardEnabled()) {
            log.info("[java_fallback] AI forward disabled (base-url not set), path={}", path);
            return null;
        }

        String url = aiServiceBaseUrl + path;
        try {
            String jsonBody = (requestBody instanceof String)
                    ? (String) requestBody
                    : objectMapper.writeValueAsString(requestBody);

            log.info("[python_ai_service] Forwarding POST {}", url);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                log.info("[python_ai_service] OK — path={}", path);
                return objectMapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
            } else {
                log.warn("[python_ai_service] Non-200 status={} for path={}, falling back to java_fallback",
                        response.statusCode(), path);
                return null;
            }
        } catch (java.net.http.HttpTimeoutException e) {
            log.warn("[python_ai_service] Timeout ({}s) for path={}, falling back to java_fallback",
                    timeoutSeconds, path);
            return null;
        } catch (java.net.ConnectException e) {
            log.warn("[python_ai_service] Connection refused for path={}, falling back to java_fallback — {}",
                    path, e.getMessage());
            return null;
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.warn("[python_ai_service] Failed to parse response JSON for path={}, falling back to java_fallback — {}",
                    path, e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("[python_ai_service] Unexpected error for path={}, falling back to java_fallback — {}: {}",
                    path, e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    // ==================== 内部方法 ====================

    /**
     * 对指定 URL 做健康检查（GET 请求，期望 200）。
     */
    private boolean healthCheck(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(Math.min(timeoutSeconds, 5)))
                    .GET()
                    .build();
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            boolean ok = response.statusCode() >= 200 && response.statusCode() < 300;
            if (ok) {
                log.debug("Health check OK: {}", url);
            }
            return ok;
        } catch (Exception e) {
            log.debug("Health check failed for {}: {}", url, e.getMessage());
            return false;
        }
    }
}
