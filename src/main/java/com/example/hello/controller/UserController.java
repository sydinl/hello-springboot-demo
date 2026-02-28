package com.example.hello.controller;

import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.hello.common.ApiResponse;
import com.example.hello.common.RateLimit;
import com.example.hello.entity.UserInfo;
import com.example.hello.service.UserService;
import com.example.hello.util.TokenUtil;

@RestController
@RequestMapping("/api/user")
@RateLimit(maxRequests = 200, timeWindow = 60, message = "用户API调用频率过高，请稍后再试")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TokenUtil tokenUtil;
    
    // 用户登录 - 更严格的限流
    @PostMapping("/login")
    @RateLimit(maxRequests = 10, timeWindow = 60, perUser = true, message = "登录尝试过于频繁，请稍后再试")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String phone = loginData.get("phone");
        String password = loginData.get("password");
        Map<String, Object> result = userService.login(phone, password);
        return ResponseEntity.ok(result);
    }
    
    // 获取用户信息 - 使用token认证
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserInfo>> getUserInfo(@RequestHeader("Authorization") String authorization) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "认证失败，请重新登录"));
            }
            
            UserInfo userInfo = userService.getUserInfo(UUID.fromString(userId));
            return ResponseEntity.ok(ApiResponse.success(userInfo));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(3001, "获取用户信息失败：" + e.getMessage()));
        }
    }
    
    // 更新用户信息 - 使用token认证
    @PutMapping("/info")
    public ResponseEntity<ApiResponse<UserInfo>> updateUserInfo(@RequestHeader("Authorization") String authorization, @RequestBody UserInfo userInfo) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "认证失败，请重新登录"));
            }
            
            UserInfo updatedUserInfo = userService.updateUserInfo(UUID.fromString(userId), userInfo);
            return ResponseEntity.ok(ApiResponse.success(updatedUserInfo));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(3001, "更新用户信息失败：" + e.getMessage()));
        }
    }
    
    // 修改密码 - 使用token认证，严格限流
    @PutMapping("/password")
    @RateLimit(maxRequests = 5, timeWindow = 300, perUser = true, message = "密码修改过于频繁，请5分钟后再试")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> changePassword(@RequestHeader("Authorization") String authorization, @RequestBody Map<String, String> passwordData) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "认证失败，请重新登录"));
            }
            
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");
            boolean success = userService.changePassword(UUID.fromString(userId), oldPassword, newPassword);
            return ResponseEntity.ok(ApiResponse.success(Map.of("success", success)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(3001, "修改密码失败：" + e.getMessage()));
        }
    }
}