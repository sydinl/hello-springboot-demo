package com.example.hello.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.hello.common.ApiResponse;
import com.example.hello.service.TencentCosService;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件上传（用户头像等）。优先腾讯云 COS，未配置时落盘到 file.upload-dir。
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${file.upload-dir:upload}")
    private String uploadDir;

    @Autowired(required = false)
    private TencentCosService tencentCosService;

    private static final long MAX_AVATAR_SIZE = 2 * 1024 * 1024; // 2MB
    private static final long MAX_PROJECT_IMAGE_SIZE = 3 * 1024 * 1024; // 3MB
    private static final String[] ALLOWED_EXT = { ".jpg", ".jpeg", ".png", ".gif", ".webp" };

    /**
     * 上传用户头像。若已配置腾讯云 COS 则上传到 COS 并返回完整 URL；否则保存到本地，返回相对路径（前端需拼接 baseUrl）。
     */
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("上传头像失败：文件为空");
            return ApiResponse.error("请选择图片");
        }
        log.info("收到头像上传请求, originalName={}, size={} bytes",
                 file.getOriginalFilename(), file.getSize());
        if (file.getSize() > MAX_AVATAR_SIZE) {
            log.warn("上传头像失败：文件过大 size={} > {}", file.getSize(), MAX_AVATAR_SIZE);
            return ApiResponse.error("图片大小不能超过 2MB");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            originalName = "image";
        }
        String ext = "";
        int lastDot = originalName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < originalName.length() - 1) {
            ext = originalName.substring(lastDot).toLowerCase();
        }
        if (ext.isEmpty()) {
            ext = ".jpg";
        }
        boolean allowed = false;
        for (String e : ALLOWED_EXT) {
            if (e.equals(ext)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            log.warn("上传头像失败：不支持的扩展名 {}", ext);
            return ApiResponse.error("仅支持 jpg、png、gif、webp 格式");
        }
        String fileName = "avatar_" + UUID.randomUUID().toString().replace("-", "") + ext;

        if (tencentCosService != null && tencentCosService.isEnabled()) {
            String fullUrl = tencentCosService.upload(file, fileName);
            if (fullUrl != null) {
                Map<String, String> data = new HashMap<>();
                data.put("url", fullUrl);
                return ApiResponse.success(data);
            }
            log.warn("COS 上传失败，回退到本地存储");
        }

        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            log.error("创建上传目录失败: {}", dir, e);
            return ApiResponse.error("上传目录不可用");
        }
        Path target = dir.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("保存头像失败: {}", target, e);
            return ApiResponse.error("保存失败");
        }
        String urlPath = "/upload/" + fileName;
        Map<String, String> data = new HashMap<>();
        data.put("url", urlPath);
        return ApiResponse.success(data);
    }

    /**
     * 上传项目图片。优先上传到腾讯云 COS（使用 project-images/ 前缀），否则保存到本地 upload 目录下的 project-images/ 子目录。
     */
    @PostMapping(value = "/project-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, String>> uploadProjectImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ApiResponse.error("请选择图片");
        }
        if (file.getSize() > MAX_PROJECT_IMAGE_SIZE) {
            return ApiResponse.error("项目图片大小不能超过 3MB");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            originalName = "image";
        }
        String ext = "";
        int lastDot = originalName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < originalName.length() - 1) {
            ext = originalName.substring(lastDot).toLowerCase();
        }
        if (ext.isEmpty()) {
            ext = ".jpg";
        }
        boolean allowed = false;
        for (String e : ALLOWED_EXT) {
            if (e.equals(ext)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            return ApiResponse.error("仅支持 jpg、png、gif、webp 格式");
        }
        String objectKey = "project-images/project_" + UUID.randomUUID().toString().replace("-", "") + ext;

        if (tencentCosService != null && tencentCosService.isEnabled()) {
            String fullUrl = tencentCosService.upload(file, objectKey);
            if (fullUrl != null) {
                Map<String, String> data = new HashMap<>();
                data.put("url", fullUrl);
                return ApiResponse.success(data);
            }
            log.warn("项目图片 COS 上传失败，回退到本地存储");
        }

        // 本地存储：uploadDir/project-images
        Path baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path dir = baseDir.resolve("project-images");
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            log.error("创建项目图片上传目录失败: {}", dir, e);
            return ApiResponse.error("上传目录不可用");
        }
        String fileName = "project_" + UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = dir.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("保存项目图片失败: {}", target, e);
            return ApiResponse.error("保存失败");
        }
        String urlPath = "/upload/project-images/" + fileName;
        Map<String, String> data = new HashMap<>();
        data.put("url", urlPath);
        return ApiResponse.success(data);
    }
}
