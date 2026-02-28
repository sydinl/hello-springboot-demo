package com.example.hello.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "username", nullable = false)
    private String username;
    
    @Column(name = "operation", nullable = false)
    private String operation; // 操作类型：CREATE, UPDATE, DELETE, VIEW
    
    @Column(name = "resource_type", nullable = false)
    private String resourceType; // 资源类型：PROJECT, CATEGORY, ORDER, USER
    
    @Column(name = "resource_id")
    private String resourceId; // 资源ID
    
    @Column(name = "resource_name")
    private String resourceName; // 资源名称
    
    @Column(name = "description", length = 1000)
    private String description; // 操作描述
    
    @Column(name = "request_url", length = 500)
    private String requestUrl; // 请求URL
    
    @Column(name = "request_method", length = 10)
    private String requestMethod; // 请求方法：GET, POST, PUT, DELETE
    
    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams; // 请求参数（JSON格式）
    
    @Column(name = "response_status")
    private Integer responseStatus; // 响应状态码
    
    @Column(name = "ip_address", length = 50)
    private String ipAddress; // IP地址
    
    @Column(name = "user_agent", length = 500)
    private String userAgent; // 用户代理
    
    @Column(name = "execution_time")
    private Long executionTime; // 执行时间（毫秒）
    
    @Column(name = "success")
    private Boolean success; // 操作是否成功
    
    @Column(name = "error_message", length = 1000)
    private String errorMessage; // 错误信息
    
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    // 构造函数
    public AuditLog() {}
    
    public AuditLog(String userId, String username, String operation, String resourceType) {
        this.userId = userId;
        this.username = username;
        this.operation = operation;
        this.resourceType = resourceType;
        this.success = true;
    }
}
