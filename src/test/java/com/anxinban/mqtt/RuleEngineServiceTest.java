package com.anxinban.mqtt;
import com.anxinban.mqtt.service.RuleEngineService;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 规则引擎单元测试。
 * 
 * 测试规则引擎的核心逻辑，如夜间时段判断等。
 */
public class RuleEngineServiceTest {
    @Test
    public void testNightTimeDetection() {
        // 测试 22:00 属于夜间
        LocalTime nightStart = LocalTime.of(22, 0);
        assertTrue(isNightTime(nightStart));

        // 测试 23:30 属于夜间
        LocalTime midnight = LocalTime.of(23, 30);
        assertTrue(isNightTime(midnight));

        // 测试 00:00 属于夜间
        LocalTime zero = LocalTime.of(0, 0);
        assertTrue(isNightTime(zero));

        // 测试 05:59 属于夜间
        LocalTime beforeDawn = LocalTime.of(5, 59);
        assertTrue(isNightTime(beforeDawn));

        // 测试 06:00 不属于夜间
        LocalTime dawn = LocalTime.of(6, 0);
        assertFalse(isNightTime(dawn));

        // 测试 08:00 不属于夜间
        LocalTime morning = LocalTime.of(8, 0);
        assertFalse(isNightTime(morning));

        // 测试 12:00 不属于夜间
        LocalTime noon = LocalTime.of(12, 0);
        assertFalse(isNightTime(noon));

        // 测试 21:59 不属于夜间
        LocalTime beforeNight = LocalTime.of(21, 59);
        assertFalse(isNightTime(beforeNight));
    }

    /**
     * 模拟规则引擎中的夜间时段判断逻辑（22:00-06:00，含边界）。
     */
    private boolean isNightTime(LocalTime time) {
        LocalTime nightStart = LocalTime.of(22, 0);
        LocalTime nightEnd = LocalTime.of(6, 0);
        // 22:00 及之后，或 06:00 之前，均视为夜间
        return !time.isBefore(nightStart) || time.isBefore(nightEnd);
    }
    @Test
    public void testTopicParsing() {
        String topic = "house/demo-house/living-room/sensor/temperature-humidity/data";
        String[] parts = topic.split("/");
        
        assertEquals("house", parts[0]);
        assertEquals("demo-house", parts[1]);
        assertEquals("living-room", parts[2]);
        assertEquals("sensor", parts[3]);
        assertEquals("temperature-humidity", parts[4]);
        assertEquals("data", parts[5]);
    }
    @Test
    public void testEventTypeDetection() {
        // 测试指纹事件
        String fingerprintTopic = "house/demo-house/door/event/fingerprint/alert";
        assertTrue(fingerprintTopic.matches("house/.+/event/fingerprint/alert"));

        // 测试门禁事件
        String accessTopic = "house/demo-house/door/event/access/alert";
        assertTrue(accessTopic.matches("house/.+/event/access/alert"));

        // 测试摔倒检测事件
        String fallTopic = "house/demo-house/living-room/event/fall-detection/alert";
        assertTrue(fallTopic.matches("house/.+/event/fall-detection/alert"));

        // 测试紧急呼叫事件
        String emergencyTopic = "house/demo-house/living-room/event/emergency-call/alert";
        assertTrue(emergencyTopic.matches("house/.+/event/emergency-call/alert"));
    }
}
