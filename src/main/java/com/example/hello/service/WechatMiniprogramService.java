package com.example.hello.service;

import com.example.hello.dto.WechatMiniprogramLoginRequest;
import com.example.hello.dto.WechatMiniprogramLoginResponse;

/**
 * 微信小程序服务接口
 */
public interface WechatMiniprogramService {
    
    /**
     * 微信小程序登录
     * @param request 登录请求
     * @return 登录响应
     */
    WechatMiniprogramLoginResponse login(WechatMiniprogramLoginRequest request);
    
    /**
     * 通过code获取微信用户信息
     * @param code 微信登录凭证
     * @return 微信用户信息
     */
    WechatUserInfo getWechatUserInfo(String code);
    
    /**
     * 刷新访问令牌
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    String refreshAccessToken(String refreshToken);

    /**
     * 通过微信手机号授权 code 获取用户手机号
     * @param phoneCode 小程序端 wx.getPhoneNumber 返回的 code
     * @return 解密得到的手机号（开发环境可返回模拟手机号）
     */
    String getPhoneNumberByCode(String phoneCode);
    
    /**
     * 验证访问令牌
     * @param accessToken 访问令牌
     * @return 是否有效
     */
    boolean validateAccessToken(String accessToken);
    
    /**
     * 生成小程序码（不限制数量，用于推广等）
     * @param scene 场景值，最多32个字符，如 referrerId=xxx
     * @param page 小程序页面路径，如 pages/index/index
     * @return 小程序码图片字节，失败返回 null
     */
    byte[] generateUnlimitedWxacode(String scene, String page);
    
    /**
     * 微信用户信息内部类
     */
    class WechatUserInfo {
        private String openId;
        private String unionId;
        private String sessionKey;
        private String nickname;
        private String avatarUrl;
        private Integer gender;
        private String city;
        private String province;
        private String country;
        private String language;
        
        // 构造函数
        public WechatUserInfo() {}
        
        public WechatUserInfo(String openId, String unionId, String sessionKey) {
            this.openId = openId;
            this.unionId = unionId;
            this.sessionKey = sessionKey;
        }
        
        // Getter和Setter方法
        public String getOpenId() { return openId; }
        public void setOpenId(String openId) { this.openId = openId; }
        
        public String getUnionId() { return unionId; }
        public void setUnionId(String unionId) { this.unionId = unionId; }
        
        public String getSessionKey() { return sessionKey; }
        public void setSessionKey(String sessionKey) { this.sessionKey = sessionKey; }
        
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        
        public Integer getGender() { return gender; }
        public void setGender(Integer gender) { this.gender = gender; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
    }
}
