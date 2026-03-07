package com.example.hello.service;

import com.example.hello.dto.WechatPaymentParams;
import com.example.hello.entity.Order;

/**
 * 微信支付服务（V2 JSAPI/小程序）
 */
public interface WechatPayService {

    /**
     * 生成小程序调起支付所需参数（统一下单 + 二次签名）
     *
     * @param order  订单
     * @param openId 用户在该小程序下的 openid
     * @return 调起 wx.requestPayment 所需的参数
     */
    WechatPaymentParams createJsapiPaymentParams(Order order, String openId);

    /**
     * 处理支付结果异步通知（验签、更新订单状态）
     *
     * @param xmlBody 微信 POST 的 XML  body
     * @return 是否处理成功（用于返回给微信的 return_code）
     */
    boolean handleNotify(String xmlBody);
}
