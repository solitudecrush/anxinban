package com.anxinban.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

/**
 * 高德地图逆地理编码服务 — 将经纬度坐标转换为大致区域名称。
 *
 * <p>调用高德 Web API 逆地理编码接口，返回坐标附近的大致位置描述，
 * 用于 SOS 紧急求助短信中的位置信息展示。不返回精确门牌号，保护老人隐私。</p>
 *
 * <p>API 文档：https://lbs.amap.com/api/webservice/guide/api/georegeo</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class AmapGeocodeService {

    private static final Logger log = LoggerFactory.getLogger(AmapGeocodeService.class);

    private static final String AMAP_REGEO_URL = "https://restapi.amap.com/v3/geocode/regeo";

    @Value("${amap.api.key:}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private HttpClient httpClient;

    @PostConstruct
    public void init() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        log.info("AmapGeocodeService initialized — apiKey configured: {}", apiKey != null && !apiKey.isBlank());
    }

    /**
     * 判断高德 API Key 是否已配置。
     *
     * @return true 如果 Key 非空
     */
    public boolean isEnabled() {
        return apiKey != null && !apiKey.isBlank();
    }

    /**
     * 根据经纬度坐标返回大致位置描述。
     *
     * <p>返回格式如 "朝阳区望京街道附近（116.397128,39.908042）"，
     * 如果高德 API 不可用，则仅返回坐标。</p>
     *
     * @param lngLat 经纬度，格式 "lng,lat"（如 "116.397128,39.908042"）
     * @return 位置描述字符串
     */
    public String getApproximateLocation(String lngLat) {
        if (lngLat == null || lngLat.isBlank()) {
            return "未知位置";
        }

        String[] parts = lngLat.split(",");
        if (parts.length != 2) {
            return lngLat;
        }

        String lng = parts[0].trim();
        String lat = parts[1].trim();

        if (!isEnabled()) {
            log.debug("Amap API key not configured, returning coordinates only");
            return "坐标（" + lng + "," + lat + "）";
        }

        try {
            String url = AMAP_REGEO_URL + "?location=" + lng + "," + lat
                    + "&key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
                    + "&extensions=base";
            log.debug("Calling Amap regeo API: location={},{}", lng, lat);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Map<String, Object> result = objectMapper.readValue(response.body(),
                        new TypeReference<Map<String, Object>>() {});
                return parseLocationResult(result, lng, lat);
            } else {
                log.warn("Amap regeo API returned non-200 status: {}", response.statusCode());
                return "坐标（" + lng + "," + lat + "）";
            }
        } catch (Exception e) {
            log.warn("Amap regeo API call failed: {}", e.getMessage());
            return "坐标（" + lng + "," + lat + "）";
        }
    }

    /**
     * 解析高德逆地理编码返回结果，提取大致区域名。
     *
     * <p>提取优先级：商圈 &gt; 街道 &gt; 区县，不返回详细门牌号。</p>
     */
    @SuppressWarnings("unchecked")
    private String parseLocationResult(Map<String, Object> result, String lng, String lat) {
        try {
            Map<String, Object> regeocode = (Map<String, Object>) result.get("regeocode");
            if (regeocode == null) {
                return "坐标（" + lng + "," + lat + "）";
            }

            // 优先用 addressComponent
            Map<String, Object> addressComponent = (Map<String, Object>) regeocode.get("addressComponent");
            if (addressComponent != null) {
                // 尝试取商圈
                Object businessAreasObj = addressComponent.get("businessAreas");
                if (businessAreasObj instanceof java.util.List && !((java.util.List<?>) businessAreasObj).isEmpty()) {
                    Object first = ((java.util.List<?>) businessAreasObj).get(0);
                    if (first instanceof Map) {
                        String businessArea = (String) ((Map<String, Object>) first).get("name");
                        if (businessArea != null && !businessArea.isBlank() && !"[]".equals(businessArea)) {
                            return businessArea + "附近（" + lng + "," + lat + "）";
                        }
                    }
                }

                // 取街道 + 区县组合
                String street = (String) addressComponent.get("streetNumber");
                String township = (String) addressComponent.get("township");
                String district = (String) addressComponent.get("district");
                String city = (String) addressComponent.get("city");

                if (street != null && !street.isBlank() && !"[]".equals(street)) {
                    String prefix = (district != null && !district.isBlank()) ? district : (city != null ? city : "");
                    return (prefix.isEmpty() ? street : prefix + street) + "附近（" + lng + "," + lat + "）";
                }

                if (township != null && !township.isBlank() && !"[]".equals(township)) {
                    String prefix = (district != null && !district.isBlank()) ? district : (city != null ? city : "");
                    return prefix + township + "附近（" + lng + "," + lat + "）";
                }

                if (district != null && !district.isBlank() && !"[]".equals(district)) {
                    String prefix = (city != null && !city.isBlank()) ? city : "";
                    return prefix + district + "附近（" + lng + "," + lat + "）";
                }
            }

            // 回退：使用 formatted_address 的简要描述
            String formattedAddress = (String) regeocode.get("formatted_address");
            if (formattedAddress != null && !formattedAddress.isBlank()) {
                // 简化地址，只取前两段
                String[] addrParts = formattedAddress.split("(?<=省|市|区|县|街道|镇|乡)");
                if (addrParts.length >= 2) {
                    return addrParts[0] + addrParts[1] + "附近（" + lng + "," + lat + "）";
                }
                return formattedAddress + "附近（" + lng + "," + lat + "）";
            }

            return "坐标（" + lng + "," + lat + "）";
        } catch (Exception e) {
            log.warn("Failed to parse Amap regeo result: {}", e.getMessage());
            return "坐标（" + lng + "," + lat + "）";
        }
    }
}
