package com.example.hello.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.hello.dto.WechatMiniprogramLoginRequest;
import com.example.hello.dto.WechatMiniprogramLoginResponse;
import com.example.hello.entity.User;
import com.example.hello.repository.UserRepository;
import com.example.hello.service.WechatMiniprogramService;
import com.example.hello.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * 微信小程序服务实现类
 */
@Service
@Slf4j
public class WechatMiniprogramServiceImpl implements WechatMiniprogramService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Value("${wechat.miniprogram.appid:}")
    private String appId;
    
    @Value("${wechat.miniprogram.secret:}")
    private String appSecret;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private volatile String cachedWechatAccessToken;
    private volatile long cachedWechatTokenExpiresAt;
    
    private static final String WECHAT_CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";
    private static final String WECHAT_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    private static final String WECHAT_GETWXACODE_UNLIMIT_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";
    
    @Override
    public WechatMiniprogramLoginResponse login(WechatMiniprogramLoginRequest request) {
        try {
            // 1. 通过code获取微信用户信息
            WechatUserInfo wechatUserInfo = getWechatUserInfo(request.getCode());
            if (wechatUserInfo == null || wechatUserInfo.getOpenId() == null) {
                throw new RuntimeException("获取微信用户信息失败");
            }
            
            // 2. 查找或创建用户
            User user = findOrCreateUser(wechatUserInfo, request);
            
            // 3. 生成JWT令牌
            String accessToken = generateAccessToken(user);
            
            // 4. 构建响应
            return new WechatMiniprogramLoginResponse(
                accessToken,
                7200L, // 2小时过期
                user.getId(),
                user.getFullName() != null ? user.getFullName() : user.getUsername(),
                user.getAvatar(),
                false, // 这里可以根据实际情况判断是否为新用户
                wechatUserInfo.getOpenId(),
                wechatUserInfo.getUnionId(),
                user.getRole(),
                user.getEnabled()
            );
            
        } catch (Exception e) {
            log.error("微信小程序登录失败", e);
            throw new RuntimeException("登录失败：" + e.getMessage());
        }
    }
    
    @Override
    public WechatUserInfo getWechatUserInfo(String code) {
        try {
            // 开发阶段使用mock数据
            if (isDevelopmentMode()) {
                return generateMockWechatUserInfo(code);
            }
            
            // 生产环境调用真实微信API
            return getRealWechatUserInfo(code);
            
        } catch (Exception e) {
            log.error("获取微信用户信息失败", e);
            return null;
        }
    }
    
    /**
     * 判断是否为开发模式
     */
    private boolean isDevelopmentMode() {
        // 可以通过配置文件或环境变量控制
        // 这里简单判断appId和appSecret是否为空
        return appId == null || appId.isEmpty() || appSecret == null || appSecret.isEmpty();
    }
    
    /**
     * 生成mock微信用户信息
     */
    private WechatUserInfo generateMockWechatUserInfo(String code) {
        log.info("开发模式：生成mock微信用户信息，code: {}", code);
        
        WechatUserInfo userInfo = new WechatUserInfo();
        
        // 基于code生成固定的openId，确保同一code返回相同结果
        String mockOpenId = "mock_openid_" + Math.abs(code.hashCode());
        userInfo.setOpenId(mockOpenId);
        userInfo.setSessionKey("mock_session_key_" + System.currentTimeMillis());
        userInfo.setUnionId("mock_unionid_" + Math.abs(code.hashCode()));
        
        log.info("生成mock用户信息: openId={}, unionId={}", mockOpenId, userInfo.getUnionId());
        return userInfo;
    }
    
    /**
     * 调用真实微信API获取用户信息
     */
    private WechatUserInfo getRealWechatUserInfo(String code) {
        try {
            // 构建请求参数
            Map<String, String> params = new HashMap<>();
            params.put("appid", appId);
            params.put("secret", appSecret);
            params.put("js_code", code);
            params.put("grant_type", "authorization_code");
            
            // 调用微信API
            String url = WECHAT_CODE2SESSION_URL + "?appid=" + appId + 
                        "&secret=" + appSecret + "&js_code=" + code + 
                        "&grant_type=authorization_code";
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String responseBody = response.getBody();
            
            log.info("微信API响应: {}", responseBody);
            
            // 解析响应
            JsonNode jsonNode;
            try {
                jsonNode = objectMapper.readTree(responseBody);
            } catch (JsonProcessingException e) {
                log.error("解析微信API响应失败", e);
                throw new RuntimeException("解析微信API响应失败", e);
            }
            
            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
                String errmsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                throw new RuntimeException("微信API调用失败: " + errmsg);
            }
            
            // 提取用户信息
            WechatUserInfo userInfo = new WechatUserInfo();
            userInfo.setOpenId(jsonNode.get("openid").asText());
            userInfo.setSessionKey(jsonNode.get("session_key").asText());
            
            if (jsonNode.has("unionid")) {
                userInfo.setUnionId(jsonNode.get("unionid").asText());
            }
            
            return userInfo;
            
        } catch (Exception e) {
            log.error("调用真实微信API失败", e);
            throw e;
        }
    }
    
    @Override
    public String refreshAccessToken(String refreshToken) {
        // 这里可以实现刷新令牌的逻辑
        // 目前简单返回原令牌
        return refreshToken;
    }
    
    @Override
    public boolean validateAccessToken(String accessToken) {
        try {
            return jwtUtil.validateToken(accessToken);
        } catch (Exception e) {
            log.error("验证访问令牌失败", e);
            return false;
        }
    }
    
    /**
     * 查找或创建用户
     */
    private User findOrCreateUser(WechatUserInfo wechatUserInfo, WechatMiniprogramLoginRequest request) {
        // 根据OpenID查找用户
        Optional<User> existingUser = userRepository.findByOpenId(wechatUserInfo.getOpenId());
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // 更新用户信息
            updateUserInfo(user, wechatUserInfo, request);
            return userRepository.save(user);
        } else {
            // 创建新用户
            User newUser = createNewUser(wechatUserInfo, request);
            return userRepository.save(newUser);
        }
    }
    
    /**
     * 更新用户信息
     */
    private void updateUserInfo(User user, WechatUserInfo wechatUserInfo, WechatMiniprogramLoginRequest request) {
        if (request.getNickname() != null && !request.getNickname().isEmpty()) {
            user.setFullName(request.getNickname());
        }
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
            user.setAvatar(request.getAvatarUrl());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender() == 1 ? "男" : request.getGender() == 2 ? "女" : "未知");
        }
        if (request.getCity() != null && !request.getCity().isEmpty()) {
            user.setCity(request.getCity());
        }
        if (request.getProvince() != null && !request.getProvince().isEmpty()) {
            user.setProvince(request.getProvince());
        }
        if (request.getCountry() != null && !request.getCountry().isEmpty()) {
            user.setCountry(request.getCountry());
        }
        if (request.getLanguage() != null && !request.getLanguage().isEmpty()) {
            user.setLanguage(request.getLanguage());
        }
        
        // 更新微信相关信息
        user.setOpenId(wechatUserInfo.getOpenId());
        if (wechatUserInfo.getUnionId() != null) {
            user.setUnionId(wechatUserInfo.getUnionId());
        }
        user.setLastLoginTime(new Date());
    }
    
    /**
     * 创建新用户
     */
    private User createNewUser(WechatUserInfo wechatUserInfo, WechatMiniprogramLoginRequest request) {
        User user = new User();
        
        // 设置基本信息
        user.setUsername("wx_" + wechatUserInfo.getOpenId().substring(0, Math.min(8, wechatUserInfo.getOpenId().length())));
        user.setPassword(""); // 微信登录用户不需要密码
        user.setPhone(generateMockPhoneNumber(wechatUserInfo.getOpenId())); // 生成模拟电话号码避免唯一约束冲突
        user.setFullName(request.getNickname() != null ? request.getNickname() : "微信用户");
        user.setRole("USER");
        user.setGender(request.getGender() != null ? 
            (request.getGender() == 1 ? "男" : request.getGender() == 2 ? "女" : "未知") : "未知");
        user.setAvatar(request.getAvatarUrl());
        user.setEnabled(true);
        user.setPoints(0);
        user.setBalance(0.0);
        user.setMemberLevel("普通会员");
        
        // 设置微信相关信息
        user.setOpenId(wechatUserInfo.getOpenId());
        if (wechatUserInfo.getUnionId() != null) {
            user.setUnionId(wechatUserInfo.getUnionId());
        }
        
        // 设置位置信息
        user.setCity(request.getCity());
        user.setProvince(request.getProvince());
        user.setCountry(request.getCountry());
        user.setLanguage(request.getLanguage());
        
        // 设置时间
        user.setLastLoginTime(new Date());
        
        return user;
    }
    
    /**
     * 生成模拟电话号码
     * 基于OpenID生成一个唯一的模拟手机号，格式：1XX + OpenID的hash值后8位
     */
    private String generateMockPhoneNumber(String openId) {
        // 使用OpenID的hashCode生成一个数字
        int hash = Math.abs(openId.hashCode());
        
        // 生成一个以1开头的11位手机号
        // 1XX + 8位数字，确保是有效的手机号格式
        String phoneNumber = String.format("1%02d%08d", 
            (hash % 90) + 10, // 10-99，确保第二位是0-9
            hash % 100000000  // 8位数字
        );
        
        return phoneNumber;
    }
    
    /**
     * 生成访问令牌
     */
    private String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        claims.put("openId", user.getOpenId());
        
        return jwtUtil.generateToken(user.getUsername(), claims);
    }

    /**
     * 获取微信接口 access_token（用于调用 getwxacodeunlimit 等），带简单缓存
     */
    private String getWechatAccessToken() {
        if (cachedWechatAccessToken != null && System.currentTimeMillis() < cachedWechatTokenExpiresAt) {
            return cachedWechatAccessToken;
        }
        if (appId == null || appId.isEmpty() || appSecret == null || appSecret.isEmpty()) {
            log.warn("小程序 appId/secret 未配置，无法生成小程序码");
            return null;
        }
        String url = WECHAT_TOKEN_URL + "?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
        try {
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            JsonNode node = objectMapper.readTree(resp.getBody());
            if (node.has("errcode") && node.get("errcode").asInt() != 0) {
                log.error("获取微信 access_token 失败: {}", resp.getBody());
                return null;
            }
            cachedWechatAccessToken = node.get("access_token").asText();
            int expiresIn = node.has("expires_in") ? node.get("expires_in").asInt() : 7200;
            cachedWechatTokenExpiresAt = System.currentTimeMillis() + (expiresIn - 300) * 1000L;
            return cachedWechatAccessToken;
        } catch (Exception e) {
            log.error("获取微信 access_token 异常", e);
            return null;
        }
    }

    @Override
    public byte[] generateUnlimitedWxacode(String scene, String page) {
        String token = getWechatAccessToken();
        if (token == null) return null;
        String url = WECHAT_GETWXACODE_UNLIMIT_URL + "?access_token=" + token;
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("scene", scene != null && scene.length() > 32 ? scene.substring(0, 32) : scene);
            body.put("page", page != null && !page.isEmpty() ? page : "pages/index/index");
            body.put("width", 280);
            body.put("check_path", false);
            String bodyJson = objectMapper.writeValueAsString(body);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<byte[]> resp = restTemplate.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(bodyJson, headers), byte[].class);
            byte[] bytes = resp.getBody();
            if (bytes == null || bytes.length == 0) return null;
            if (bytes[0] == '{') {
                JsonNode node = objectMapper.readTree(bytes);
                log.error("生成小程序码失败: {}", node.toString());
                return null;
            }
            return bytes;
        } catch (Exception e) {
            log.error("生成小程序码异常", e);
            return null;
        }
    }
}
