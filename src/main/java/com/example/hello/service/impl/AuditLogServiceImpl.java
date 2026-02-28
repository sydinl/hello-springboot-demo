package com.example.hello.service.impl;

import com.example.hello.entity.AuditLog;
import com.example.hello.repository.AuditLogRepository;
import com.example.hello.service.AuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Override
    @Transactional
    public AuditLog saveAuditLog(AuditLog auditLog) {
        try {
            log.debug("保存审计日志：{}", auditLog.getDescription());
            return auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("保存审计日志失败", e);
            throw new RuntimeException("保存审计日志失败", e);
        }
    }
    
    @Override
    public AuditLog getAuditLogById(String id) {
        try {
            return auditLogRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("审计日志不存在，ID：" + id));
        } catch (Exception e) {
            log.error("查询审计日志失败，ID：{}", id, e);
            throw new RuntimeException("查询审计日志失败", e);
        }
    }
    
    @Override
    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        try {
            return auditLogRepository.findAll(pageable);
        } catch (Exception e) {
            log.error("查询审计日志列表失败", e);
            throw new RuntimeException("查询审计日志列表失败", e);
        }
    }
    
    @Override
    public Page<AuditLog> getAuditLogsWithConditions(
            String userId, String operation, String resourceType, 
            String username, String ipAddress, Boolean success,
            LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        try {
            return auditLogRepository.findAuditLogsWithConditions(
                    userId, operation, resourceType, username, ipAddress, 
                    success, startTime, endTime, pageable);
        } catch (Exception e) {
            log.error("根据条件查询审计日志失败", e);
            throw new RuntimeException("根据条件查询审计日志失败", e);
        }
    }
    
    @Override
    public Page<AuditLog> getAuditLogsByUserId(String userId, Pageable pageable) {
        try {
            return auditLogRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
        } catch (Exception e) {
            log.error("根据用户ID查询审计日志失败，用户ID：{}", userId, e);
            throw new RuntimeException("根据用户ID查询审计日志失败", e);
        }
    }
    
    @Override
    public Page<AuditLog> getAuditLogsByOperation(String operation, Pageable pageable) {
        try {
            return auditLogRepository.findByOperationOrderByCreateTimeDesc(operation, pageable);
        } catch (Exception e) {
            log.error("根据操作类型查询审计日志失败，操作类型：{}", operation, e);
            throw new RuntimeException("根据操作类型查询审计日志失败", e);
        }
    }
    
    @Override
    public Page<AuditLog> getAuditLogsByResourceType(String resourceType, Pageable pageable) {
        try {
            return auditLogRepository.findByResourceTypeOrderByCreateTimeDesc(resourceType, pageable);
        } catch (Exception e) {
            log.error("根据资源类型查询审计日志失败，资源类型：{}", resourceType, e);
            throw new RuntimeException("根据资源类型查询审计日志失败", e);
        }
    }
    
    @Override
    public Page<AuditLog> getAuditLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        try {
            return auditLogRepository.findByCreateTimeBetweenOrderByCreateTimeDesc(startTime, endTime, pageable);
        } catch (Exception e) {
            log.error("根据时间范围查询审计日志失败", e);
            throw new RuntimeException("根据时间范围查询审计日志失败", e);
        }
    }
    
    @Override
    public List<AuditLog> getRecentAuditLogs(int limit) {
        try {
            return auditLogRepository.findTop10ByOrderByCreateTimeDesc();
        } catch (Exception e) {
            log.error("查询最近审计日志失败", e);
            throw new RuntimeException("查询最近审计日志失败", e);
        }
    }
    
    @Override
    public Page<AuditLog> getFailedAuditLogs(Pageable pageable) {
        try {
            return auditLogRepository.findBySuccessFalseOrderByCreateTimeDesc(pageable);
        } catch (Exception e) {
            log.error("查询失败审计日志失败", e);
            throw new RuntimeException("查询失败审计日志失败", e);
        }
    }
    
    @Override
    public Map<String, Long> countOperationsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            List<Object[]> results = auditLogRepository.countOperationsByTimeRange(startTime, endTime);
            Map<String, Long> counts = new HashMap<>();
            for (Object[] result : results) {
                counts.put((String) result[0], (Long) result[1]);
            }
            return counts;
        } catch (Exception e) {
            log.error("统计操作次数失败", e);
            throw new RuntimeException("统计操作次数失败", e);
        }
    }
    
    @Override
    public Map<String, Long> countResourceTypesByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            List<Object[]> results = auditLogRepository.countResourceTypesByTimeRange(startTime, endTime);
            Map<String, Long> counts = new HashMap<>();
            for (Object[] result : results) {
                counts.put((String) result[0], (Long) result[1]);
            }
            return counts;
        } catch (Exception e) {
            log.error("统计资源类型操作次数失败", e);
            throw new RuntimeException("统计资源类型操作次数失败", e);
        }
    }
    
    @Override
    @Transactional
    public void deleteAuditLogsBefore(LocalDateTime beforeTime) {
        try {
            auditLogRepository.deleteByCreateTimeBefore(beforeTime);
            log.info("删除{}之前的审计日志完成", beforeTime);
        } catch (Exception e) {
            log.error("删除审计日志失败", e);
            throw new RuntimeException("删除审计日志失败", e);
        }
    }
    
    @Override
    @Transactional
    public void logAudit(String userId, String username, String operation, String resourceType, 
                        String resourceId, String resourceName, String description, 
                        String requestUrl, String requestMethod, String requestParams,
                        Integer responseStatus, String ipAddress, String userAgent,
                        Long executionTime, Boolean success, String errorMessage) {
        try {
            AuditLog auditLog = new AuditLog(userId, username, operation, resourceType);
            auditLog.setResourceId(resourceId);
            auditLog.setResourceName(resourceName);
            auditLog.setDescription(description);
            auditLog.setRequestUrl(requestUrl);
            auditLog.setRequestMethod(requestMethod);
            auditLog.setRequestParams(requestParams);
            auditLog.setResponseStatus(responseStatus);
            auditLog.setIpAddress(ipAddress);
            auditLog.setUserAgent(userAgent);
            auditLog.setExecutionTime(executionTime);
            auditLog.setSuccess(success);
            auditLog.setErrorMessage(errorMessage);
            
            saveAuditLog(auditLog);
        } catch (Exception e) {
            log.error("记录审计日志失败", e);
            // 审计日志记录失败不应该影响主业务流程，只记录错误日志
        }
    }
}
