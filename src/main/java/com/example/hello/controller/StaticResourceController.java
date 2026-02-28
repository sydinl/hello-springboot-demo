package com.example.hello.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/static")
public class StaticResourceController {

    @GetMapping("/items/{filename}")
    public ResponseEntity<byte[]> getItemImage(@PathVariable String filename) {
        try {
            Resource resource = new ClassPathResource("static/items/" + filename);
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] data = Files.readAllBytes(resource.getFile().toPath());
            
            // 根据文件扩展名设置Content-Type
            String contentType = getContentType(filename);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(data.length);
            headers.setCacheControl("max-age=3600"); // 缓存1小时
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
                    
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private String getContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "svg":
                return "image/svg+xml";
            case "webp":
                return "image/webp";
            default:
                return "application/octet-stream";
        }
    }
}
