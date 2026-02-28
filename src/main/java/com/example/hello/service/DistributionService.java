package com.example.hello.service;

import com.example.hello.entity.DistributionData;
import com.example.hello.entity.DistributionOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface DistributionService {
    // 获取分销中心数据
    DistributionData getDistributionData(UUID userId);
    
    // 获取分销订单列表
    Page<DistributionOrder> getDistributionOrders(UUID userId, Pageable pageable);
    
    // 获取指定状态的分销订单
    Page<DistributionOrder> getDistributionOrdersByStatus(UUID userId, String status, Pageable pageable);
}