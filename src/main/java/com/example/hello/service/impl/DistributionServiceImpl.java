package com.example.hello.service.impl;

import com.example.hello.entity.DistributionData;
import com.example.hello.entity.DistributionOrder;
import com.example.hello.repository.DistributionOrderRepository;
import com.example.hello.service.DistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DistributionServiceImpl implements DistributionService {

    @Autowired
    private DistributionOrderRepository distributionOrderRepository;

    @Override
    public DistributionData getDistributionData(UUID userId) {
        // 模拟获取分销中心数据
        DistributionData data = new DistributionData();
        data.setTotalCommission(5000.0);
        data.setAvailableCommission(3000.0);
        data.setTeamCount(50);
        data.setTodayOrderCount(10);
        return data;
    }

    @Override
    public Page<DistributionOrder> getDistributionOrders(UUID userId, Pageable pageable) {
        // 模拟获取分销订单列表
        return Page.empty(pageable);
    }

    @Override
    public Page<DistributionOrder> getDistributionOrdersByStatus(UUID userId, String status, Pageable pageable) {
        // 模拟获取指定状态的分销订单
        return Page.empty(pageable);
    }
}