package com.example.hello.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class HttpsConfig {

    @Value("${custom.server.port.http}")
    private int httpPort;

    @Value("${server.port}")
    private int httpsPort;

    /**
     * 配置Tomcat服务器，同时支持HTTP和HTTPS
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
        return factory -> {
            // 配置HTTP端口
            factory.addAdditionalTomcatConnectors(
                    // 创建HTTP连接器
                    new org.apache.catalina.connector.Connector(
                            org.apache.coyote.http11.Http11NioProtocol.class.getName()) {
                        {
                            setScheme("http");
                            setPort(httpPort);
                            setSecure(false);
                            // 将HTTP请求重定向到HTTPS
                            setRedirectPort(httpsPort);
                        }
                    }
            );
        };
    }

    /**
     * 配置CORS跨域
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    /**
     * 配置处理转发请求的过滤器，确保正确处理X-Forwarded-*头
     */
    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}