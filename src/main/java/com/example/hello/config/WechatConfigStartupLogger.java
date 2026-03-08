package com.example.hello.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 启动时打印微信支付、小程序配置是否完整，便于排查问题。敏感信息脱敏显示。
 */
@Component
@Order(1)
@Slf4j
public class WechatConfigStartupLogger implements CommandLineRunner {

    private final WechatPayConfig wechatPayConfig;

    @Value("${wechat.miniprogram.appid:}")
    private String miniprogramAppid;

    @Value("${wechat.miniprogram.secret:}")
    private String miniprogramSecret;

    public WechatConfigStartupLogger(WechatPayConfig wechatPayConfig) {
        this.wechatPayConfig = wechatPayConfig;
    }

    @Override
    public void run(String... args) {
        log.info("========== 微信相关配置检查 ==========");

        // 微信支付
        String mchId = wechatPayConfig.getMchId();
        String appId = wechatPayConfig.getAppId();
        String apiKey = wechatPayConfig.getApiKey();
        String notifyUrl = wechatPayConfig.getNotifyUrl();
        String certPath = wechatPayConfig.getCertPath();

        log.info("  [微信支付] mch-id:        {} {}", mask(mchId, false), status(mchId));
        log.info("  [微信支付] app-id:        {} {}", mask(appId, false), status(appId));
        log.info("  [微信支付] api-key:       {} {}", mask(apiKey, true), status(apiKey));
        log.info("  [微信支付] notify-url:    {} {}", mask(notifyUrl, false), status(notifyUrl));
        log.info("  [微信支付] cert-path:     {} (可选，退款等需证书时再配)", certPath != null && !certPath.isEmpty() ? "已配置" : "未配置");

        boolean payOk = has(mchId) && has(appId) && has(apiKey) && has(notifyUrl);
        if (payOk) {
            log.info("  [微信支付] 结论: 配置完整，将走真实支付");
        } else {
            log.warn("  [微信支付] 结论: 缺少必填项(mchId/appId/apiKey/notifyUrl)，将走模拟支付");
        }

        // 微信小程序
        log.info("  [小程序]   appid:         {} {}", mask(miniprogramAppid, false), status(miniprogramAppid));
        log.info("  [小程序]   secret:        {} {}", mask(miniprogramSecret, true), status(miniprogramSecret));

        boolean mpOk = has(miniprogramAppid) && has(miniprogramSecret);
        if (mpOk) {
            log.info("  [小程序]   结论: 配置完整，小程序登录可用");
        } else {
            log.warn("  [小程序]   结论: 缺少 appid 或 secret，小程序登录将不可用");
        }

        log.info("========================================");
    }

    private static boolean has(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static String status(String s) {
        return has(s) ? "[OK]" : "[MISSING]";
    }

    private static String mask(String s, boolean sensitive) {
        if (!has(s)) return "(空)";
        if (!sensitive) return s.length() > 20 ? s.substring(0, 12) + "..." : s;
        if (s.length() <= 4) return "***";
        return "***" + s.substring(s.length() - 4);
    }
}
