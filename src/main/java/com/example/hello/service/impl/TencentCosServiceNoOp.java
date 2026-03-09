package com.example.hello.service.impl;

import java.io.InputStream;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.hello.service.TencentCosService;

/**
 * COS 未启用时的空实现（走本地上传）
 */
@Service
@ConditionalOnProperty(name = "tencent.cos.enabled", havingValue = "false", matchIfMissing = true)
public class TencentCosServiceNoOp implements TencentCosService {

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String upload(MultipartFile file, String objectKey) {
        return null;
    }

    @Override
    public String upload(InputStream inputStream, long contentLength, String contentType, String objectKey) {
        return null;
    }
}
