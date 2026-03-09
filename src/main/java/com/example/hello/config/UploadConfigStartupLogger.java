package com.example.hello.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.example.hello.service.TencentCosService;
import lombok.extern.slf4j.Slf4j;

/**
 * 启动时打印头像/文件上传使用 COS 还是本地上传。
 */
@Component
@Order(2)
@Slf4j
public class UploadConfigStartupLogger implements CommandLineRunner {

    @Autowired(required = false)
    private TencentCosService tencentCosService;

    @Value("${file.upload-dir:upload}")
    private String uploadDir;

    @Override
    public void run(String... args) {
        log.info("========== 文件上传配置 ==========");
        if (tencentCosService != null && tencentCosService.isEnabled()) {
            log.info("  [头像/文件] 使用: 腾讯云 COS");
        } else {
            log.info("  [头像/文件] 使用: 本地上传 (file.upload-dir={})", uploadDir);
        }
        log.info("==================================");
    }
}
