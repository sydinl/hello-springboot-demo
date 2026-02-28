package com.example.hello.service;

import com.example.hello.entity.BalanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface BalanceService {
    // 获取用户余额
    Double getUserBalance(UUID userId);
    
    // 获取用户余额记录列表
    Page<BalanceRecord> getUserBalanceRecords(UUID userId, Pageable pageable);
    
    // 获取用户指定类型的余额记录
    Page<BalanceRecord> getUserBalanceRecordsByType(UUID userId, String type, Pageable pageable);
}