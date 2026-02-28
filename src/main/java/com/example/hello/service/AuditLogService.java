package com.example.hello.service;

import com.example.hello.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AuditLogService {
    
    // 保存审计日志
    AuditLog saveAuditLog(AuditLog auditLog);
    
    // 根据ID查询审计日志
    AuditLog getAuditLogById(String id);
    
    // 分页查询审计日志
    Page<AuditLog> getAuditLogs(Pageable pageable);
    
    // 根据条件查询审计日志
    Page<AuditLog> getAuditLogsWithConditions(
            String userId, String operation, String resourceType, 
            String username, String ipAddress, Boolean success,
            LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    // 根据用户ID查询审计日志
    Page<AuditLog> getAuditLogsByUserId(String userId, Pageable pageable);
    
    // 根据操作类型查询审计日志
    Page<AuditLog> getAuditLogsByOperation(String operation, Pageable pageable);
    
    // 根据资源类型查询审计日志
    Page<AuditLog> getAuditLogsByResourceType(String resourceType, Pageable pageable);
    
    // 根据时间范围查询审计日志
    Page<AuditLog> getAuditLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    // 查询最近的审计日志
    List<AuditLog> getRecentAuditLogs(int limit);
    
    // 查询失败的审计日志
    Page<AuditLog> getFailedAuditLogs(Pageable pageable);
    
    // 统计操作次数
    Map<String, Long> countOperationsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    // 统计资源类型操作次数
    Map<String, Long> countResourceTypesByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    // 删除指定时间之前的审计日志
    void deleteAuditLogsBefore(LocalDateTime beforeTime);
    
    // 记录审计日志的便捷方法
    void logAudit(String userId, String username, String operation, String resourceType, 
                  String resourceId, String resourceName, String description, 
                  String requestUrl, String requestMethod, String requestParams,
                  Integer responseStatus, String ipAddress, String userAgent,
                  Long executionTime, Boolean success, String errorMessage);
}
