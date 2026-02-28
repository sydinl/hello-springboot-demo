package com.example.hello.aspect;

import com.example.hello.common.ApiResponse;
import com.example.hello.common.RateLimit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Rate Limiting AOP切面
 * 实现基于内存的限流功能
 */
@Aspect
@Component
public class RateLimitAspect {
    
    // 限流计数器存储
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    
    // 定时清理过期数据的线程池
    private final ScheduledExecutorService cleanupExecutor = Executors.newScheduledThreadPool(1);
    
    public RateLimitAspect() {
        // 每30秒清理一次过期的限流数据
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredData, 30, 30, TimeUnit.SECONDS);
    }
    
    /**
     * 限流信息内部类
     */
    private static class RateLimitInfo {
        private final long firstRequestTime;
        private volatile int requestCount;
        private final int maxRequests;
        private final long timeWindow;
        
        public RateLimitInfo(int maxRequests, long timeWindow) {
            this.firstRequestTime = System.currentTimeMillis();
            this.requestCount = 1;
            this.maxRequests = maxRequests;
            this.timeWindow = timeWindow;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - firstRequestTime >= timeWindow * 1000;
        }
        
        public boolean isAllowed() {
            return requestCount <= maxRequests;
        }
        
        public void increment() {
            this.requestCount++;
        }
        
        public void reset() {
            this.requestCount = 1;
        }
    }
    
    @Around("@annotation(com.example.hello.common.RateLimit) || @within(com.example.hello.common.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取方法上的@RateLimit注解，如果没有则获取类上的
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        if (rateLimit == null) {
            rateLimit = method.getDeclaringClass().getAnnotation(RateLimit.class);
        }
        
        if (rateLimit == null) {
            return joinPoint.proceed();
        }
        
        // 构建限流键
        String key = buildRateLimitKey(rateLimit, method, joinPoint);
        
        // 检查是否超过限流阈值
        if (isRateLimited(key, rateLimit)) {
            return createRateLimitResponse(rateLimit);
        }
        
        // 执行原方法
        return joinPoint.proceed();
    }
    
    /**
     * 清理过期的限流数据
     */
    private void cleanupExpiredData() {
        rateLimitMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    /**
     * 构建限流键
     */
    private String buildRateLimitKey(RateLimit rateLimit, Method method, ProceedingJoinPoint joinPoint) {
        StringBuilder keyBuilder = new StringBuilder("rate_limit:");
        
        // 添加键前缀
        if (!rateLimit.keyPrefix().isEmpty()) {
            keyBuilder.append(rateLimit.keyPrefix()).append(":");
        } else {
            keyBuilder.append(method.getDeclaringClass().getSimpleName())
                     .append(".")
                     .append(method.getName())
                     .append(":");
        }
        
        // 如果按用户限流，添加用户标识
        if (rateLimit.perUser()) {
            String userId = getCurrentUserId();
            if (userId != null) {
                keyBuilder.append("user:").append(userId);
            } else {
                // 如果无法获取用户ID，使用IP地址
                String clientIp = getClientIpAddress();
                keyBuilder.append("ip:").append(clientIp);
            }
        } else {
            // 全局限流，使用IP地址
            String clientIp = getClientIpAddress();
            keyBuilder.append("ip:").append(clientIp);
        }
        
        return keyBuilder.toString();
    }
    
    /**
     * 检查是否超过限流阈值
     */
    private boolean isRateLimited(String key, RateLimit rateLimit) {
        try {
            // 使用内存中的ConcurrentHashMap实现计数器
            RateLimitInfo rateLimitInfo = rateLimitMap.compute(key, (k, existing) -> {
                if (existing == null) {
                    // 第一次访问，创建新的限流信息
                    return new RateLimitInfo(rateLimit.maxRequests(), rateLimit.timeWindow());
                } else if (existing.isExpired()) {
                    // 已过期，重置计数器
                    existing.reset();
                    return existing;
                } else {
                    // 在时间窗口内，增加计数
                    existing.increment();
                    return existing;
                }
            });
            
            // 检查是否超过限制
            return !rateLimitInfo.isAllowed();
        } catch (Exception e) {
            // 异常时，记录日志但不阻止请求
            System.err.println("Rate limiting check failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 创建限流响应
     */
    private Object createRateLimitResponse(RateLimit rateLimit) {
        return ApiResponse.error(429, rateLimit.message());
    }
    
    /**
     * 获取当前用户ID
     * 这里需要根据实际的认证机制来实现
     */
    private String getCurrentUserId() {
        try {
            // 从请求头或JWT token中获取用户ID
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String userId = request.getHeader("X-User-ID");
                if (userId != null && !userId.isEmpty()) {
                    return userId;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to get user ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // 检查X-Forwarded-For头
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
                    return xForwardedFor.split(",")[0].trim();
                }
                
                // 检查X-Real-IP头
                String xRealIp = request.getHeader("X-Real-IP");
                if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
                    return xRealIp;
                }
                
                // 使用RemoteAddr
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            System.err.println("Failed to get client IP: " + e.getMessage());
        }
        return "unknown";
    }
}
