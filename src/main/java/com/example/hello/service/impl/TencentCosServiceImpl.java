package com.example.hello.service.impl;

import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.hello.service.TencentCosService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * 腾讯云 COS 上传实现。需配置 tencent.cos.enabled=true 且 secret-id、bucket、region 非空。
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "tencent.cos.enabled", havingValue = "true")
public class TencentCosServiceImpl implements TencentCosService {

    @Value("${tencent.cos.secret-id:}")
    private String secretId;

    @Value("${tencent.cos.secret-key:}")
    private String secretKey;

    @Value("${tencent.cos.bucket:}")
    private String bucket;

    @Value("${tencent.cos.region:ap-guangzhou}")
    private String region;

    @Value("${tencent.cos.base-path:avatar/}")
    private String basePath;

    @Value("${tencent.cos.cdn-domain:}")
    private String cdnDomain;

    private COSClient cosClient;

    @PostConstruct
    public void init() {
        if (secretId == null || secretId.isBlank() || secretKey == null || secretKey.isBlank()
                || bucket == null || bucket.isBlank()) {
            log.warn("腾讯云 COS 已启用但 secret-id/bucket 未配置，COS 上传将不可用");
            return;
        }
        try {
            COSCredentials cred = new BasicCOSCredentials(secretId.trim(), secretKey.trim());
            ClientConfig config = new ClientConfig(new Region(region == null ? "ap-guangzhou" : region.trim()));
            cosClient = new COSClient(cred, config);
            log.info("腾讯云 COS 客户端已初始化，bucket={}, region={}", bucket, region);
        } catch (Exception e) {
            log.error("初始化腾讯云 COS 客户端失败", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (cosClient != null) {
            try {
                cosClient.shutdown();
            } catch (Exception e) {
                log.warn("关闭 COS 客户端异常", e);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return cosClient != null && bucket != null && !bucket.isBlank();
    }

    @Override
    public String upload(MultipartFile file, String objectKey) {
        if (file == null || file.isEmpty() || objectKey == null || objectKey.isBlank()) {
            return null;
        }
        String key = basePath == null || basePath.isBlank() ? objectKey
                : (basePath.endsWith("/") ? basePath + objectKey : basePath + "/" + objectKey);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            String contentType = file.getContentType();
            if (contentType != null && !contentType.isBlank()) {
                metadata.setContentType(contentType);
            }
            return upload(file.getInputStream(), file.getSize(), contentType, key);
        } catch (Exception e) {
            log.error("上传文件到 COS 失败, key={}", key, e);
            return null;
        }
    }

    @Override
    public String upload(InputStream inputStream, long contentLength, String contentType, String objectKey) {
        if (!isEnabled() || inputStream == null || objectKey == null || objectKey.isBlank()) {
            return null;
        }
        String key = objectKey;
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            if (contentType != null && !contentType.isBlank()) {
                metadata.setContentType(contentType);
            }
            PutObjectRequest request = new PutObjectRequest(bucket, key, inputStream, metadata);
            PutObjectResult result = cosClient.putObject(request);
            if (result != null) {
                return buildPublicUrl(key);
            }
        } catch (Exception e) {
            log.error("上传文件到 COS 失败, key={}", key, e);
        }
        return null;
    }

    private String buildPublicUrl(String objectKey) {
        String key = objectKey.startsWith("/") ? objectKey.substring(1) : objectKey;
        if (cdnDomain != null && !cdnDomain.isBlank()) {
            String domain = cdnDomain.trim();
            if (!domain.startsWith("http")) {
                domain = "https://" + domain;
            }
            return domain.endsWith("/") ? domain + key : domain + "/" + key;
        }
        return "https://" + bucket + ".cos." + region + ".myqcloud.com/" + key;
    }
}
