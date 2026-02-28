package com.example.hello.service;

import com.example.hello.entity.PointRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface PointService {
    // 获取用户积分余额
    Integer getUserPoints(UUID userId);
    
    // 获取用户积分记录列表
    Page<PointRecord> getUserPointRecords(UUID userId, Pageable pageable);
    
    // 获取用户指定类型的积分记录
    Page<PointRecord> getUserPointRecordsByType(UUID userId, String type, Pageable pageable);
}