package com.example.hello.controller;

import com.example.hello.common.ApiResponse;
import com.example.hello.entity.AuditLog;
import com.example.hello.service.AuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/audit")
@Slf4j
public class AuditLogController {
    
    @Autowired
    private AuditLogService auditLogService;
    
    // 审计日志管理页面
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String auditLogManagementPage(Model model) {
        log.info("访问审计日志管理页面");
        return "admin/audit-management";
    }
    
    // ========== 审计日志管理API接口 ==========
    
    // 获取审计日志列表（分页）
    @GetMapping("/api/list")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ApiResponse<Map<String, Object>> getAuditLogList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            log.info("获取审计日志列表，页码：{}，每页数量：{}", page, pageSize);
            
            // 创建分页和排序对象
            Sort sort = sortDir.equals("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
            
            // 解析时间参数
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            if (startTime != null && !startTime.isEmpty()) {
                startDateTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            if (endTime != null && !endTime.isEmpty()) {
                endDateTime = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            
            // 获取审计日志列表
            Page<AuditLog> auditLogs = auditLogService.getAuditLogsWithConditions(
                    userId, operation, resourceType, username, ipAddress, 
                    success, startDateTime, endDateTime, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", auditLogs.getTotalElements());
            result.put("list", auditLogs.getContent());
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("totalPages", auditLogs.getTotalPages());
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取审计日志列表失败", e);
            return ApiResponse.error("获取审计日志列表失败：" + e.getMessage());
        }
    }
    
    // 获取审计日志详情
    @GetMapping("/api/detail")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ApiResponse<AuditLog> getAuditLogDetail(@RequestParam String auditLogId) {
        try {
            log.info("获取审计日志详情，审计日志ID：{}", auditLogId);
            AuditLog auditLog = auditLogService.getAuditLogById(auditLogId);
            return ApiResponse.success(auditLog);
        } catch (Exception e) {
            log.error("获取审计日志详情失败，审计日志ID：{}", auditLogId, e);
            return ApiResponse.error("获取审计日志详情失败：" + e.getMessage());
        }
    }
    
    // 获取最近的操作日志
    @GetMapping("/api/recent")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ApiResponse<List<AuditLog>> getRecentAuditLogs(@RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("获取最近审计日志，数量：{}", limit);
            List<AuditLog> auditLogs = auditLogService.getRecentAuditLogs(limit);
            return ApiResponse.success(auditLogs);
        } catch (Exception e) {
            log.error("获取最近审计日志失败", e);
            return ApiResponse.error("获取最近审计日志失败：" + e.getMessage());
        }
    }
    
    // 获取失败的操作日志
    @GetMapping("/api/failed")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ApiResponse<Map<String, Object>> getFailedAuditLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            log.info("获取失败审计日志，页码：{}，每页数量：{}", page, pageSize);
            
            Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createTime").descending());
            Page<AuditLog> auditLogs = auditLogService.getFailedAuditLogs(pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", auditLogs.getTotalElements());
            result.put("list", auditLogs.getContent());
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("totalPages", auditLogs.getTotalPages());
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取失败审计日志失败", e);
            return ApiResponse.error("获取失败审计日志失败：" + e.getMessage());
        }
    }
    
    // 获取操作统计
    @GetMapping("/api/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ApiResponse<Map<String, Object>> getAuditStatistics(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        try {
            log.info("获取审计统计信息");
            
            // 解析时间参数
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            if (startTime != null && !startTime.isEmpty()) {
                startDateTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            if (endTime != null && !endTime.isEmpty()) {
                endDateTime = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            
            // 如果没有指定时间，默认查询最近7天
            if (startDateTime == null) {
                startDateTime = LocalDateTime.now().minusDays(7);
            }
            if (endDateTime == null) {
                endDateTime = LocalDateTime.now();
            }
            
            Map<String, Long> operationCounts = auditLogService.countOperationsByTimeRange(startDateTime, endDateTime);
            Map<String, Long> resourceTypeCounts = auditLogService.countResourceTypesByTimeRange(startDateTime, endDateTime);
            
            Map<String, Object> result = new HashMap<>();
            result.put("operationCounts", operationCounts);
            result.put("resourceTypeCounts", resourceTypeCounts);
            result.put("startTime", startDateTime);
            result.put("endTime", endDateTime);
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取审计统计信息失败", e);
            return ApiResponse.error("获取审计统计信息失败：" + e.getMessage());
        }
    }
    
    // 清理历史审计日志
    @DeleteMapping("/api/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ApiResponse<Void> cleanupAuditLogs(@RequestParam String beforeTime) {
        try {
            log.info("清理审计日志，清理时间：{}", beforeTime);
            
            LocalDateTime beforeDateTime = LocalDateTime.parse(beforeTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            auditLogService.deleteAuditLogsBefore(beforeDateTime);
            
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("清理审计日志失败", e);
            return ApiResponse.error("清理审计日志失败：" + e.getMessage());
        }
    }
}
