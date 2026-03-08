package com.example.hello.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 云开发存储工具类
 * 用于将云开发的 FileID 转换为 HTTPS 链接，以便在网页端展示
 */
@Slf4j
public class CloudStorageUtil {
    
    /**
     * 将云开发 FileID 转换为 HTTPS URL
     * 
     * FileID 格式: cloud://your-env-id.xxxx-xxxx-xxxx/your-image-path.jpg
     * HTTPS URL 格式: https://xxxx-xxxx-xxxx.tcb.qcloud.la/your-image-path.jpg
     * 
     * @param fileId 云开发 FileID（以 cloud:// 开头）
     * @return HTTPS URL，如果不是 cloud:// 开头则返回原值
     */
    public static String convertFileIdToUrl(String fileId) {
        if (fileId == null || fileId.isEmpty()) {
            return fileId;
        }
        
        // 如果不是 cloud:// 开头，直接返回原值（可能是普通 HTTP/HTTPS URL）
        if (!fileId.startsWith("cloud://")) {
            return fileId;
        }
        
        try {
            // 移除 cloud:// 前缀
            String path = fileId.substring(8); // "cloud://" 长度为 8
            
            // 查找第一个斜杠，分隔环境ID和文件路径
            int firstSlashIndex = path.indexOf('/');
            if (firstSlashIndex == -1) {
                log.warn("无效的云开发 FileID 格式: {}", fileId);
                return fileId;
            }
            
            // 提取环境ID（格式：your-env-id.xxxx-xxxx-xxxx）
            String envId = path.substring(0, firstSlashIndex);
            
            // 提取文件路径（去掉环境ID后的部分）
            String filePath = path.substring(firstSlashIndex + 1);
            
            // 从环境ID中提取实际的云存储ID（xxxx-xxxx-xxxx 部分）
            // 环境ID格式通常是：your-env-id.xxxx-xxxx-xxxx
            String cloudId;
            int dotIndex = envId.lastIndexOf('.');
            if (dotIndex != -1 && dotIndex < envId.length() - 1) {
                // 提取点号后的部分作为云存储ID
                cloudId = envId.substring(dotIndex + 1);
            } else {
                // 如果没有点号，使用整个环境ID
                cloudId = envId;
            }
            
            // 拼接 HTTPS URL
            String httpsUrl = "https://" + cloudId + ".tcb.qcloud.la/" + filePath;
            
            log.debug("转换云开发 FileID: {} -> {}", fileId, httpsUrl);
            return httpsUrl;
            
        } catch (Exception e) {
            log.error("转换云开发 FileID 失败: {}", fileId, e);
            return fileId; // 转换失败时返回原值
        }
    }
    
    /**
     * 判断是否为云开发 FileID
     * 
     * @param url 图片 URL
     * @return 如果是 cloud:// 开头返回 true，否则返回 false
     */
    public static boolean isCloudFileId(String url) {
        return url != null && url.startsWith("cloud://");
    }
}
