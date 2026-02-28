package com.example.hello.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API调用频率限制注解
 * 用于标记需要限制调用频率的API方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    
    /**
     * 时间窗口大小（秒）
     * 默认60秒
     */
    int timeWindow() default 60;
    
    /**
     * 最大请求次数
     * 默认100次
     */
    int maxRequests() default 100;
    
    /**
     * 限流键的前缀
     * 默认使用类名+方法名
     */
    String keyPrefix() default "";
    
    /**
     * 是否基于用户ID进行限流
     * 如果为true，则每个用户独立计算限流
     * 如果为false，则全局限流
     */
    boolean perUser() default false;
    
    /**
     * 限流失败时的错误消息
     */
    String message() default "API调用频率过高，请稍后再试";
}



