package com.example.hello.dto;

import lombok.Data;

/**
 * 微信小程序登录响应DTO
 */
@Data
public class WechatMiniprogramLoginResponse {
    
    /**
     * 访问令牌
     */
    private String accessToken;
    
    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";
    
    /**
     * 过期时间（秒）
     */
    private Long expiresIn;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户头像
     */
    private String avatarUrl;
    
    /**
     * 是否新用户
     */
    private Boolean isNewUser;
    
    /**
     * 微信OpenID
     */
    private String openId;
    
    /**
     * 微信UnionID（如果有）
     */
    private String unionId;
    
    /**
     * 用户角色
     */
    private String role;
    
    /**
     * 用户状态
     */
    private Boolean enabled;
    
    public WechatMiniprogramLoginResponse() {
    }
    
    public WechatMiniprogramLoginResponse(String accessToken, Long expiresIn, String userId, 
                                        String nickname, String avatarUrl, Boolean isNewUser,
                                        String openId, String unionId, String role, Boolean enabled) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.isNewUser = isNewUser;
        this.openId = openId;
        this.unionId = unionId;
        this.role = role;
        this.enabled = enabled;
    }
}
