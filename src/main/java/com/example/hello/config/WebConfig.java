package com.example.hello.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:upload}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        String location = uploadDir.startsWith("/") ? uploadDir : "./" + uploadDir;
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + location)
                .setCachePeriod(3600);
        // 配置静态资源映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600); // 缓存1小时
        
        // 配置图片资源映射
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(3600);
        
        // 配置CSS资源映射
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCachePeriod(3600);
        
        // 配置JS资源映射
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCachePeriod(3600);
        
        // 配置items目录映射
        registry.addResourceHandler("/static/items/**")
                .addResourceLocations("classpath:/static/items/")
                .setCachePeriod(3600);
    }
}
