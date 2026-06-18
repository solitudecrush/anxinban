package com.anxinban.mqtt.simulator;

import com.anxinban.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

/**
 * 设备模拟器控制接口。
 *
 * 作用：提供 REST API 手动控制模拟器的启停，并触发各类硬件事件，
 * 方便开发调试时无需真实硬件即可验证云端数据链路。
 */
@RestController
@RequestMapping("/api/simulator")
public class SimulatorController {
    /** 字段含义待补充 */

    private final DeviceSimulator deviceSimulator;

    public SimulatorController(DeviceSimulator deviceSimulator) {
        this.deviceSimulator = deviceSimulator;
    }
    @PostMapping("/start")
    public ApiResponse<String> start() {
        deviceSimulator.start();
        return ApiResponse.success("模拟器已启动");
    }
    @PostMapping("/stop")
    public ApiResponse<String> stop() {
        deviceSimulator.stop();
        return ApiResponse.success("模拟器已停止");
    }
    @GetMapping("/status")
    public ApiResponse<Boolean> status() {
        return ApiResponse.success(deviceSimulator.isRunning());
    }
    @PostMapping("/fingerprint/success")
    public ApiResponse<String> fingerprintSuccess(@RequestParam(defaultValue = "user-001") String userId) {
        deviceSimulator.triggerFingerprintSuccess(userId);
        return ApiResponse.success("已触发指纹开锁成功");
    }
    @PostMapping("/fingerprint/fail")
    public ApiResponse<String> fingerprintFail() {
        deviceSimulator.triggerFingerprintFail();
        return ApiResponse.success("已触发指纹开锁失败及陌生人抓拍");
    }
    @PostMapping("/curtain")
    public ApiResponse<String> curtain(@RequestParam String command,
                                       @RequestParam(required = false) Integer percent) {
        deviceSimulator.triggerCurtain(command, percent);
        return ApiResponse.success("已触发窗帘控制：" + command);
    }
    @PostMapping("/buzzer")
    public ApiResponse<String> buzzer(@RequestParam String command,
                                      @RequestParam(required = false) String reason) {
        deviceSimulator.triggerBuzzer(command, reason);
        return ApiResponse.success("已触发蜂鸣器：" + command);
    }
    @PostMapping("/light")
    public ApiResponse<String> light(@RequestParam String command,
                                     @RequestParam(required = false) Integer brightness) {
        deviceSimulator.triggerLight(command, brightness);
        return ApiResponse.success("已触发灯光控制：" + command);
    }
    @PostMapping("/watch/emergency")
    public ApiResponse<String> watchEmergency() {
        deviceSimulator.triggerWatchEmergency();
        return ApiResponse.success("已触发手表紧急呼救");
    }
    @PostMapping("/fall")
    public ApiResponse<String> fallDetection(@RequestParam(defaultValue = "living-room") String room) {
        deviceSimulator.triggerFallDetection(room);
        return ApiResponse.success("已触发摔倒检测事件：" + room);
    }
    @PostMapping("/find-item")
    public ApiResponse<String> findItem(@RequestParam String itemName,
                                        @RequestParam(defaultValue = "living-room") String room) {
        deviceSimulator.triggerFindItem(itemName, room);
        return ApiResponse.success("已触发查找物品：" + itemName);
    }
}
