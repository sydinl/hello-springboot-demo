package com.example.hello.service;

import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

/**
 * 腾讯云 COS 上传（可选实现：配置后头像等文件上传到 COS，返回公网/CDN URL）
 */
public interface TencentCosService {

    /**
     * 是否已启用 COS（配置了 secretId、bucket 等）
     */
    boolean isEnabled();

    /**
     * 上传文件到 COS，返回可公网访问的完整 URL。
     * 若未启用 COS 或上传失败，返回 null。
     *
     * @param file 上传文件
     * @param objectKey 对象键，如 avatar/xxx.jpg
     * @return 完整 URL，或 null
     */
    String upload(MultipartFile file, String objectKey);

    /**
     * 使用输入流上传，需指定长度和 contentType。
     */
    String upload(InputStream inputStream, long contentLength, String contentType, String objectKey);
}
