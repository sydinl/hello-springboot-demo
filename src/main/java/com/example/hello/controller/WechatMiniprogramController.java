package com.example.hello.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.hello.annotation.AuditLog;
import com.example.hello.common.ApiResponse;
import com.example.hello.common.RateLimit;
import com.example.hello.dto.WechatMiniprogramLoginRequest;
import com.example.hello.dto.WechatMiniprogramLoginResponse;
import com.example.hello.entity.UserInfo;
import com.example.hello.service.UserService;
import com.example.hello.service.WechatMiniprogramService;
import com.example.hello.util.TokenUtil;
import jakarta.validation.Valid;

/**
 * 微信小程序控制器
 */
@RestController
@RequestMapping("/api/wechat/miniprogram")
@RateLimit(maxRequests = 100, timeWindow = 60, message = "微信小程序API调用频率过高，请稍后再试")
public class WechatMiniprogramController {
    
    @Autowired
    private WechatMiniprogramService wechatMiniprogramService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenUtil tokenUtil;
    
    /**
     * 微信小程序登录
     */
    @PostMapping("/login")
    @AuditLog(operation = "LOGIN", resourceType = "USER", description = "微信小程序登录")
    public ApiResponse<WechatMiniprogramLoginResponse> login(@Valid @RequestBody WechatMiniprogramLoginRequest request) {
        try {
            WechatMiniprogramLoginResponse response = wechatMiniprogramService.login(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("登录失败：" + e.getMessage());
        }
    }
    
    /**
     * 刷新访问令牌
     */
    @PostMapping("/refresh-token")
    public ApiResponse<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ApiResponse.error("刷新令牌不能为空");
            }
            
            String newAccessToken = wechatMiniprogramService.refreshAccessToken(refreshToken);
            
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", "7200");
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("刷新令牌失败：" + e.getMessage());
        }
    }
    
    /**
     * 验证访问令牌
     */
    @PostMapping("/validate-token")
    public ApiResponse<Map<String, Object>> validateToken(@RequestBody Map<String, String> request) {
        try {
            String accessToken = request.get("accessToken");
            if (accessToken == null || accessToken.isEmpty()) {
                return ApiResponse.error("访问令牌不能为空");
            }
            
            boolean isValid = wechatMiniprogramService.validateAccessToken(accessToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("message", isValid ? "令牌有效" : "令牌无效或已过期");
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("验证令牌失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取微信用户信息（需要先登录）
     */
    @GetMapping("/userinfo")
    public ApiResponse<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String authorization) {
        try {
            // 从Authorization头中提取token
            String token = authorization.replace("Bearer ", "");
            
            // 验证token
            if (!wechatMiniprogramService.validateAccessToken(token)) {
                return ApiResponse.error("访问令牌无效");
            }
            
            // 这里可以根据token解析用户信息
            // 实际实现中需要从JWT中解析用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("message", "用户信息获取成功");
            
            return ApiResponse.success(userInfo);
        } catch (Exception e) {
            return ApiResponse.error("获取用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 绑定微信手机号（通过 getPhoneNumber 返回的 code）
     */
    @PostMapping("/bind-phone")
    public ApiResponse<Map<String, Object>> bindPhone(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                      @RequestBody Map<String, String> request) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null || userId.isEmpty()) {
                return ApiResponse.error(2001, "认证失败，请重新登录");
            }
            String code = request != null ? request.get("code") : null;
            if (code == null || code.isEmpty()) {
                return ApiResponse.error("手机号授权 code 不能为空");
            }
            // 1. 调用微信接口解出手机号
            String phoneNumber = wechatMiniprogramService.getPhoneNumberByCode(code);
            // 2. 通过用户服务更新手机号（内部已做唯一性校验）
            UserInfo info = new UserInfo();
            info.setPhone(phoneNumber);
            UserInfo updated = userService.updateUserInfo(UUID.fromString(userId), info);

            Map<String, Object> data = new HashMap<>();
            data.put("phoneNumber", updated.getPhone());
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error("绑定手机号失败：" + e.getMessage());
        }
    }
}
