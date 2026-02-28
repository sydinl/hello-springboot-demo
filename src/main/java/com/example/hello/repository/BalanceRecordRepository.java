package com.example.hello.repository;

import com.example.hello.entity.BalanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BalanceRecordRepository extends JpaRepository<BalanceRecord, UUID> {
    // 按用户ID分页查询余额记录
    Page<BalanceRecord> findByUserId(UUID userId, Pageable pageable);
    
    // 按用户ID和类型分页查询余额记录
    Page<BalanceRecord> findByUserIdAndType(UUID userId, String type, Pageable pageable);
}