package com.example.hello.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付配置
 */
@Configuration
@ConfigurationProperties(prefix = "wechat.pay")
@Data
public class WechatPayConfig {
    
    /**
     * 微信支付商户号
     */
    private String mchId;
    
    /**
     * 微信支付应用ID
     */
    private String appId;
    
    /**
     * 微信支付API密钥
     */
    private String apiKey;
    
    /**
     * 微信支付证书路径
     */
    private String certPath;
    
    /**
     * 微信支付API基础URL
     */
    private String baseUrl = "https://api.mch.weixin.qq.com";
    
    /**
     * 支付回调通知URL
     */
    private String notifyUrl;
    
    /**
     * 支付超时时间（分钟）
     */
    private Integer timeoutMinutes = 30;
}



