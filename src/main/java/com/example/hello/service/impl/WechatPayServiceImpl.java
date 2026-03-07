package com.example.hello.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.example.hello.config.WechatPayConfig;
import com.example.hello.dto.WechatPaymentParams;
import com.example.hello.entity.Order;
import com.example.hello.repository.OrderRepository;
import com.example.hello.service.WechatPayService;
import lombok.extern.slf4j.Slf4j;

/**
 * 微信支付 V2 JSAPI/小程序 实现：统一下单 + 调起支付二次签名 + 支付回调验签
 */
@Service
@Slf4j
public class WechatPayServiceImpl implements WechatPayService {

    private static final String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    private static final String SIGN_TYPE_MD5 = "MD5";
    private static final String TRADE_TYPE_JSAPI = "JSAPI";

    @Autowired
    private WechatPayConfig wechatPayConfig;

    @Autowired
    private OrderRepository orderRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public WechatPaymentParams createJsapiPaymentParams(Order order, String openId) {
        if (wechatPayConfig.getMchId() == null || wechatPayConfig.getMchId().isEmpty()
                || wechatPayConfig.getAppId() == null || wechatPayConfig.getAppId().isEmpty()
                || wechatPayConfig.getApiKey() == null || wechatPayConfig.getApiKey().isEmpty()) {
            log.warn("微信支付未配置完整(mchId/appId/apiKey)，返回模拟参数");
            return buildMockParams(order);
        }

        try {
            // 1. 统一下单
            double amount = order.getFinalAmount() != null ? order.getFinalAmount() : order.getTotalPrice();
            int totalFee = (int) Math.round(amount * 100); // 元 -> 分
            if (totalFee <= 0) {
                throw new IllegalArgumentException("订单金额必须大于0");
            }

            String nonceStr = UUID.randomUUID().toString().replace("-", "");
            String outTradeNo = order.getOrderNo();
            String body = "订单-" + (outTradeNo != null ? outTradeNo : order.getOrderId());
            String notifyUrl = wechatPayConfig.getNotifyUrl();
            if (notifyUrl == null || notifyUrl.isEmpty()) {
                log.warn("wechat.pay.notify-url 未配置，支付回调将无法收到");
            }

            Map<String, String> params = new TreeMap<>();
            params.put("appid", wechatPayConfig.getAppId());
            params.put("mch_id", wechatPayConfig.getMchId());
            params.put("nonce_str", nonceStr);
            params.put("body", body.length() > 127 ? body.substring(0, 127) : body);
            params.put("out_trade_no", outTradeNo);
            params.put("total_fee", String.valueOf(totalFee));
            params.put("spbill_create_ip", "127.0.0.1");
            params.put("notify_url", notifyUrl != null ? notifyUrl : "");
            params.put("trade_type", TRADE_TYPE_JSAPI);
            params.put("openid", openId);

            String sign = signMd5(params, wechatPayConfig.getApiKey());
            params.put("sign", sign);

            String xml = buildXml(params);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            HttpEntity<String> entity = new HttpEntity<>(xml, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(UNIFIED_ORDER_URL, entity, String.class);
            String respBody = response.getBody();
            log.info("统一下单响应: {}", respBody != null && respBody.length() > 200 ? respBody.substring(0, 200) + "..." : respBody);

            Map<String, String> respMap = parseXmlToMap(respBody);
            String returnCode = respMap.get("return_code");
            String resultCode = respMap.get("result_code");
            if (!"SUCCESS".equals(returnCode)) {
                String returnMsg = respMap.get("return_msg");
                throw new RuntimeException("统一下单通信失败: " + returnMsg);
            }
            if (!"SUCCESS".equals(resultCode)) {
                String errCode = respMap.get("err_code");
                String errCodeDes = respMap.get("err_code_des");
                throw new RuntimeException("统一下单业务失败: " + errCode + " " + errCodeDes);
            }

            String prepayId = respMap.get("prepay_id");
            if (prepayId == null || prepayId.isEmpty()) {
                throw new RuntimeException("统一下单未返回 prepay_id");
            }

            // 保存 prepay_id 到订单（可选，便于对账）
            order.setWechatPrepayId(prepayId);
            orderRepository.save(order);

            // 2. 二次签名（小程序调起支付）
            long timeStamp = System.currentTimeMillis() / 1000;
            String mpNonce = UUID.randomUUID().toString().replace("-", "");
            String packageVal = "prepay_id=" + prepayId;

            Map<String, String> signParams = new TreeMap<>();
            signParams.put("appId", wechatPayConfig.getAppId());
            signParams.put("nonceStr", mpNonce);
            signParams.put("package", packageVal);
            signParams.put("signType", SIGN_TYPE_MD5);
            signParams.put("timeStamp", String.valueOf(timeStamp));
            String paySign = signMd5(signParams, wechatPayConfig.getApiKey());

            WechatPaymentParams result = new WechatPaymentParams();
            result.setTimeStamp(String.valueOf(timeStamp));
            result.setNonceStr(mpNonce);
            result.setPackageValue(packageVal);
            result.setSignType(SIGN_TYPE_MD5);
            result.setPaySign(paySign);
            return result;

        } catch (Exception e) {
            log.error("生成微信支付参数失败, orderId={}", order.getOrderId(), e);
            throw new RuntimeException("获取支付参数失败: " + e.getMessage());
        }
    }

    @Override
    public boolean handleNotify(String xmlBody) {
        if (xmlBody == null || xmlBody.isEmpty()) {
            return false;
        }
        try {
            Map<String, String> map = parseXmlToMap(xmlBody);
            String returnCode = map.get("return_code");
            if (!"SUCCESS".equals(returnCode)) {
                log.warn("支付回调 return_code 非 SUCCESS: {}", map.get("return_msg"));
                return true; // 仍返回成功，避免微信重复推送
            }
            String sign = map.remove("sign");
            if (sign == null || wechatPayConfig.getApiKey() == null) {
                log.warn("支付回调缺少 sign 或 apiKey 未配置");
                return false;
            }
            String calculatedSign = signMd5(new TreeMap<>(map), wechatPayConfig.getApiKey());
            if (!calculatedSign.equals(sign)) {
                log.warn("支付回调验签失败");
                return false;
            }
            String resultCode = map.get("result_code");
            if (!"SUCCESS".equals(resultCode)) {
                log.warn("支付回调 result_code 非 SUCCESS: {}", map.get("err_code_des"));
                return true;
            }
            String outTradeNo = map.get("out_trade_no");
            String transactionId = map.get("transaction_id");
            if (outTradeNo == null || outTradeNo.isEmpty()) {
                log.warn("支付回调缺少 out_trade_no");
                return false;
            }
            updateOrderPaid(outTradeNo, transactionId);
            log.info("支付回调处理成功, out_trade_no={}, transaction_id={}", outTradeNo, transactionId);
            return true;
        } catch (Exception e) {
            log.error("处理支付回调异常", e);
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    protected void updateOrderPaid(String orderNo, String transactionId) {
        orderRepository.findByOrderNo(orderNo).ifPresent(order -> {
            order.setStatus("paid");
            if (transactionId != null) {
                order.setWechatTransactionId(transactionId);
            }
            order.setPayTime(new Date());
            orderRepository.save(order);
        });
    }

    private String signMd5(Map<String, String> params, String apiKey) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : params.entrySet()) {
            if ("sign".equals(e.getKey()) || e.getValue() == null || e.getValue().isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(e.getKey()).append('=').append(e.getValue());
        }
        sb.append("&key=").append(apiKey);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                hex.append(String.format("%02X", b & 0xff));
            }
            return hex.toString();
        } catch (Exception ex) {
            throw new RuntimeException("MD5 签名失败", ex);
        }
    }

    private String buildXml(Map<String, String> params) {
        StringBuilder sb = new StringBuilder("<xml>");
        for (Map.Entry<String, String> e : params.entrySet()) {
            if (e.getValue() == null) {
                continue;
            }
            sb.append("<").append(e.getKey()).append("><![CDATA[").append(e.getValue()).append("]]></").append(e.getKey()).append(">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    private static final Pattern XML_TAG = Pattern.compile("<([^>!/]+)>([^<]*(?:<!\\[CDATA\\[([^]]*)\\]\\]>)?[^<]*)</\\1>");

    private Map<String, String> parseXmlToMap(String xml) {
        Map<String, String> map = new TreeMap<>();
        if (xml == null) {
            return map;
        }
        Matcher m = XML_TAG.matcher(xml);
        while (m.find()) {
            String tag = m.group(1);
            String raw = m.group(2);
            String value = m.group(3) != null ? m.group(3) : raw;
            if (value != null) {
                map.put(tag.trim(), value.trim());
            }
        }
        return map;
    }

    private WechatPaymentParams buildMockParams(Order order) {
        WechatPaymentParams params = new WechatPaymentParams();
        params.setTimeStamp(String.valueOf(System.currentTimeMillis() / 1000));
        params.setNonceStr(UUID.randomUUID().toString().replace("-", ""));
        params.setPackageValue("prepay_id=wx" + System.currentTimeMillis());
        params.setSignType(SIGN_TYPE_MD5);
        params.setPaySign("mock_sign_" + System.currentTimeMillis());
        return params;
    }
}
