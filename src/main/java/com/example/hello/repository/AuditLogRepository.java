package com.example.hello.repository;

import com.example.hello.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {
    
    // 根据用户ID查询审计日志
    Page<AuditLog> findByUserIdOrderByCreateTimeDesc(String userId, Pageable pageable);
    
    // 根据操作类型查询审计日志
    Page<AuditLog> findByOperationOrderByCreateTimeDesc(String operation, Pageable pageable);
    
    // 根据资源类型查询审计日志
    Page<AuditLog> findByResourceTypeOrderByCreateTimeDesc(String resourceType, Pageable pageable);
    
    // 根据时间范围查询审计日志
    Page<AuditLog> findByCreateTimeBetweenOrderByCreateTimeDesc(
            LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    // 根据用户名查询审计日志
    Page<AuditLog> findByUsernameContainingOrderByCreateTimeDesc(String username, Pageable pageable);
    
    // 根据IP地址查询审计日志
    Page<AuditLog> findByIpAddressOrderByCreateTimeDesc(String ipAddress, Pageable pageable);
    
    // 根据操作是否成功查询审计日志
    Page<AuditLog> findBySuccessOrderByCreateTimeDesc(Boolean success, Pageable pageable);
    
    // 复合查询：根据多个条件查询审计日志
    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:userId IS NULL OR a.userId = :userId) AND " +
           "(:operation IS NULL OR a.operation = :operation) AND " +
           "(:resourceType IS NULL OR a.resourceType = :resourceType) AND " +
           "(:username IS NULL OR a.username LIKE %:username%) AND " +
           "(:ipAddress IS NULL OR a.ipAddress = :ipAddress) AND " +
           "(:success IS NULL OR a.success = :success) AND " +
           "(:startTime IS NULL OR a.createTime >= :startTime) AND " +
           "(:endTime IS NULL OR a.createTime <= :endTime)")
    Page<AuditLog> findAuditLogsWithConditions(
            @Param("userId") String userId,
            @Param("operation") String operation,
            @Param("resourceType") String resourceType,
            @Param("username") String username,
            @Param("ipAddress") String ipAddress,
            @Param("success") Boolean success,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);
    
    // 统计指定时间范围内的操作次数
    @Query("SELECT a.operation, COUNT(a) FROM AuditLog a WHERE " +
           "a.createTime >= :startTime AND a.createTime <= :endTime " +
           "GROUP BY a.operation")
    List<Object[]> countOperationsByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                            @Param("endTime") LocalDateTime endTime);
    
    // 统计指定时间范围内的资源类型操作次数
    @Query("SELECT a.resourceType, COUNT(a) FROM AuditLog a WHERE " +
           "a.createTime >= :startTime AND a.createTime <= :endTime " +
           "GROUP BY a.resourceType")
    List<Object[]> countResourceTypesByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                               @Param("endTime") LocalDateTime endTime);
    
    // 查询最近的操作日志
    List<AuditLog> findTop10ByOrderByCreateTimeDesc();
    
    // 查询失败的操作日志
    Page<AuditLog> findBySuccessFalseOrderByCreateTimeDesc(Pageable pageable);
    
    // 删除指定时间之前的审计日志
    void deleteByCreateTimeBefore(LocalDateTime beforeTime);
}
