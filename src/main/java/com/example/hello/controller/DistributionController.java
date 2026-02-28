package com.example.hello.controller;

import com.example.hello.entity.DistributionData;
import com.example.hello.entity.DistributionOrder;
import com.example.hello.service.DistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/distribution")
public class DistributionController {
    
    @Autowired
    private DistributionService distributionService;
    
    // 获取分销中心数据
    @GetMapping("/centerData")
    public ResponseEntity<DistributionData> getDistributionData(@RequestParam UUID userId) {
        DistributionData data = distributionService.getDistributionData(userId);
        return ResponseEntity.ok(data);
    }
    
    // 获取分销订单列表
    @GetMapping("/orders")
    public ResponseEntity<Page<DistributionOrder>> getDistributionOrders(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DistributionOrder> orders = distributionService.getDistributionOrders(userId, pageable);
        return ResponseEntity.ok(orders);
    }
    
    // 获取指定状态的分销订单
    @GetMapping("/ordersByStatus")
    public ResponseEntity<Page<DistributionOrder>> getDistributionOrdersByStatus(
            @RequestParam UUID userId,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DistributionOrder> orders = distributionService.getDistributionOrdersByStatus(userId, status, pageable);
        return ResponseEntity.ok(orders);
    }
}