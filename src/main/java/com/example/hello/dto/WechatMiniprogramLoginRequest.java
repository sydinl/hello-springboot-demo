package com.example.hello.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信小程序登录请求DTO
 */
@Data
public class WechatMiniprogramLoginRequest {
    
    /**
     * 微信小程序登录凭证code
     */
    @NotBlank(message = "登录凭证不能为空")
    private String code;
    
    /**
     * 用户昵称（可选）
     */
    private String nickname;
    
    /**
     * 用户头像URL（可选）
     */
    private String avatarUrl;
    
    /**
     * 用户性别（可选）
     * 0-未知，1-男，2-女
     */
    private Integer gender;
    
    /**
     * 用户所在城市（可选）
     */
    private String city;
    
    /**
     * 用户所在省份（可选）
     */
    private String province;
    
    /**
     * 用户所在国家（可选）
     */
    private String country;
    
    /**
     * 用户语言（可选）
     */
    private String language;
}
