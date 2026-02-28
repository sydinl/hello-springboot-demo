package com.example.hello.controller;

import com.example.hello.entity.Withdrawal;
import com.example.hello.service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/withdrawal")
public class WithdrawalController {
    
    @Autowired
    private WithdrawalService withdrawalService;
    
    // 申请提现
    @PostMapping("/apply")
    public ResponseEntity<Withdrawal> applyWithdrawal(
            @RequestParam UUID userId,
            @RequestParam Double amount) {
        Withdrawal withdrawal = withdrawalService.applyWithdrawal(userId, amount);
        return ResponseEntity.ok(withdrawal);
    }
    
    // 获取提现记录列表
    @GetMapping("/records")
    public ResponseEntity<Page<Withdrawal>> getWithdrawalRecords(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Withdrawal> records = withdrawalService.getWithdrawalRecords(userId, pageable);
        return ResponseEntity.ok(records);
    }
    
    // 获取指定状态的提现记录
    @GetMapping("/recordsByStatus")
    public ResponseEntity<Page<Withdrawal>> getWithdrawalRecordsByStatus(
            @RequestParam UUID userId,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Withdrawal> records = withdrawalService.getWithdrawalRecordsByStatus(userId, status, pageable);
        return ResponseEntity.ok(records);
    }
}