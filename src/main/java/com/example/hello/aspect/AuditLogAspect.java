package com.example.hello.aspect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.example.hello.annotation.AuditLog;
import com.example.hello.service.AuditLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class AuditLogAspect {
    
    @Autowired
    private AuditLogService auditLogService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        String userId = "anonymous";
        String username = "anonymous";
        String ipAddress = "unknown";
        String userAgent = "unknown";
        String requestUrl = "unknown";
        String requestMethod = "unknown";
        String requestParams = "";
        Integer responseStatus = 200;
        Boolean success = true;
        String errorMessage = null;
        
        try {
            // 获取当前用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                username = authentication.getName();
                userId = authentication.getName(); // 简化处理，实际项目中可能需要从用户对象获取ID
            }
            
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                ipAddress = getClientIpAddress(request);
                userAgent = request.getHeader("User-Agent");
                requestUrl = request.getRequestURL().toString();
                requestMethod = request.getMethod();
                
                // 记录请求参数
                if (auditLog.logParams()) {
                    requestParams = buildRequestParams(joinPoint, request);
                }
            }
            
            // 执行目标方法
            Object result = joinPoint.proceed();
            
            // 记录响应状态
            responseStatus = 200;
            
            return result;
            
        } catch (Exception e) {
            success = false;
            errorMessage = e.getMessage();
            responseStatus = 500;
            log.error("执行方法失败", e);
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录审计日志
            try {
                String operation = auditLog.operation().isEmpty() ? 
                    getDefaultOperation(joinPoint) : auditLog.operation();
                String resourceType = auditLog.resourceType().isEmpty() ? 
                    getDefaultResourceType(joinPoint) : auditLog.resourceType();
                String description = auditLog.description().isEmpty() ? 
                    getDefaultDescription(joinPoint) : auditLog.description();
                
                auditLogService.logAudit(
                    userId, username, operation, resourceType,
                    getResourceId(joinPoint), getResourceName(joinPoint),
                    description, requestUrl, requestMethod, requestParams,
                    responseStatus, ipAddress, userAgent, executionTime,
                    success, errorMessage
                );
            } catch (Exception e) {
                log.error("记录审计日志失败", e);
            }
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 构建请求参数
     */
    private String buildRequestParams(ProceedingJoinPoint joinPoint, HttpServletRequest request) {
        try {
            Map<String, Object> params = new HashMap<>();
            
            // 添加方法参数
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] paramValues = joinPoint.getArgs();
            
            for (int i = 0; i < paramNames.length; i++) {
                if (paramValues[i] != null) {
                    // 过滤敏感信息
                    if (isSensitiveParam(paramNames[i])) {
                        params.put(paramNames[i], "***");
                    } else {
                        params.put(paramNames[i], paramValues[i]);
                    }
                }
            }
            
            // 添加请求参数
            Map<String, String[]> requestParams = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
                if (isSensitiveParam(entry.getKey())) {
                    params.put(entry.getKey(), "***");
                } else {
                    params.put(entry.getKey(), Arrays.toString(entry.getValue()));
                }
            }
            
            return objectMapper.writeValueAsString(params);
        } catch (Exception e) {
            log.warn("构建请求参数失败", e);
            return "{}";
        }
    }
    
    /**
     * 判断是否为敏感参数
     */
    private boolean isSensitiveParam(String paramName) {
        String lowerParamName = paramName.toLowerCase();
        return lowerParamName.contains("password") || 
               lowerParamName.contains("pwd") || 
               lowerParamName.contains("secret") || 
               lowerParamName.contains("token") ||
               lowerParamName.contains("key");
    }
    
    /**
     * 获取默认操作类型
     */
    private String getDefaultOperation(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName().toLowerCase();
        if (methodName.startsWith("create") || methodName.startsWith("add") || methodName.startsWith("save")) {
            return "CREATE";
        } else if (methodName.startsWith("update") || methodName.startsWith("edit") || methodName.startsWith("modify")) {
            return "UPDATE";
        } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
            return "DELETE";
        } else if (methodName.startsWith("get") || methodName.startsWith("find") || methodName.startsWith("query") || methodName.startsWith("list")) {
            return "VIEW";
        } else {
            return "UNKNOWN";
        }
    }
    
    /**
     * 获取默认资源类型
     */
    private String getDefaultResourceType(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName().toLowerCase();
        if (className.contains("project")) {
            return "PROJECT";
        } else if (className.contains("category")) {
            return "CATEGORY";
        } else if (className.contains("order")) {
            return "ORDER";
        } else if (className.contains("user")) {
            return "USER";
        } else {
            return "UNKNOWN";
        }
    }
    
    /**
     * 获取默认描述
     */
    private String getDefaultDescription(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        return className + "." + methodName;
    }
    
    /**
     * 获取资源ID
     */
    private String getResourceId(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            // 尝试从第一个参数获取ID
            Object firstArg = args[0];
            if (firstArg instanceof String) {
                return (String) firstArg;
            } else if (firstArg != null) {
                // 尝试通过反射获取id字段
                try {
                    java.lang.reflect.Field idField = firstArg.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    Object idValue = idField.get(firstArg);
                    return idValue != null ? idValue.toString() : null;
                } catch (Exception e) {
                    // 忽略异常
                }
            }
        }
        return null;
    }
    
    /**
     * 获取资源名称
     */
    private String getResourceName(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            Object firstArg = args[0];
            if (firstArg != null) {
                // 尝试通过反射获取name字段
                try {
                    java.lang.reflect.Field nameField = firstArg.getClass().getDeclaredField("name");
                    nameField.setAccessible(true);
                    Object nameValue = nameField.get(firstArg);
                    return nameValue != null ? nameValue.toString() : null;
                } catch (Exception e) {
                    // 忽略异常
                }
            }
        }
        return null;
    }
}
