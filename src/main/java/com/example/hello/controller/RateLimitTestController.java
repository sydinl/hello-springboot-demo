package com.example.hello.controller;

import com.example.hello.common.ApiResponse;
import com.example.hello.common.RateLimit;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Rate Limiting测试控制器
 * 用于测试API限流功能
 */
@RestController
@RequestMapping("/api/test")
public class RateLimitTestController {
    
    /**
     * 测试全局限流 - 每60秒最多10次请求
     */
    @GetMapping("/global")
    @RateLimit(maxRequests = 10, timeWindow = 60, message = "全局限流测试：请求过于频繁")
    public ApiResponse<Map<String, Object>> testGlobalRateLimit() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "全局限流测试成功");
        result.put("timestamp", LocalDateTime.now());
        result.put("description", "每60秒最多10次请求");
        return ApiResponse.success(result);
    }
    
    /**
     * 测试用户限流 - 每用户每30秒最多5次请求
     */
    @GetMapping("/user")
    @RateLimit(maxRequests = 5, timeWindow = 30, perUser = true, message = "用户限流测试：请求过于频繁")
    public ApiResponse<Map<String, Object>> testUserRateLimit() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "用户限流测试成功");
        result.put("timestamp", LocalDateTime.now());
        result.put("description", "每用户每30秒最多5次请求");
        return ApiResponse.success(result);
    }
    
    /**
     * 测试严格限流 - 每10秒最多2次请求
     */
    @GetMapping("/strict")
    @RateLimit(maxRequests = 2, timeWindow = 10, message = "严格限流测试：请求过于频繁")
    public ApiResponse<Map<String, Object>> testStrictRateLimit() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "严格限流测试成功");
        result.put("timestamp", LocalDateTime.now());
        result.put("description", "每10秒最多2次请求");
        return ApiResponse.success(result);
    }
    
    /**
     * 无限流的测试接口
     */
    @GetMapping("/unlimited")
    public ApiResponse<Map<String, Object>> testUnlimited() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "无限制接口测试成功");
        result.put("timestamp", LocalDateTime.now());
        result.put("description", "此接口没有限流限制");
        return ApiResponse.success(result);
    }
}



