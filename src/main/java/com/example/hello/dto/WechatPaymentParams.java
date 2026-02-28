package com.example.hello.dto;

import lombok.Data;

/**
 * 微信支付参数DTO
 */
@Data
public class WechatPaymentParams {
    
    private String timeStamp;
    private String nonceStr;
    private String packageValue; // 对应微信的package字段
    private String signType;
    private String paySign;
    
    public WechatPaymentParams() {}
    
    public WechatPaymentParams(String timeStamp, String nonceStr, String packageValue, 
                              String signType, String paySign) {
        this.timeStamp = timeStamp;
        this.nonceStr = nonceStr;
        this.packageValue = packageValue;
        this.signType = signType;
        this.paySign = paySign;
    }
}



