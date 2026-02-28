package com.example.hello.controller;

import com.example.hello.entity.BalanceRecord;
import com.example.hello.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {
    
    @Autowired
    private BalanceService balanceService;
    
    // 获取用户余额
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Double>> getUserBalance(@RequestParam UUID userId) {
        Double balance = balanceService.getUserBalance(userId);
        return ResponseEntity.ok(Map.of("balance", balance));
    }
    
    // 获取用户余额记录列表
    @GetMapping("/records")
    public ResponseEntity<Page<BalanceRecord>> getUserBalanceRecords(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BalanceRecord> records = balanceService.getUserBalanceRecords(userId, pageable);
        return ResponseEntity.ok(records);
    }
    
    // 获取用户指定类型的余额记录
    @GetMapping("/recordsByType")
    public ResponseEntity<Page<BalanceRecord>> getUserBalanceRecordsByType(
            @RequestParam UUID userId,
            @RequestParam String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BalanceRecord> records = balanceService.getUserBalanceRecordsByType(userId, type, pageable);
        return ResponseEntity.ok(records);
    }
}