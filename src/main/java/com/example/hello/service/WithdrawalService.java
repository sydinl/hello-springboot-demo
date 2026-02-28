package com.example.hello.service;

import com.example.hello.entity.Withdrawal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface WithdrawalService {
    // 申请提现
    Withdrawal applyWithdrawal(UUID userId, Double amount);
    
    // 获取提现记录列表
    Page<Withdrawal> getWithdrawalRecords(UUID userId, Pageable pageable);
    
    // 获取指定状态的提现记录
    Page<Withdrawal> getWithdrawalRecordsByStatus(UUID userId, String status, Pageable pageable);
}