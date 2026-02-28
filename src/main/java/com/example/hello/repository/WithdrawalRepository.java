package com.example.hello.repository;

import com.example.hello.entity.Withdrawal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, UUID> {
    // 按用户ID分页查询提现记录
    Page<Withdrawal> findByUserId(UUID userId, Pageable pageable);
    
    // 按用户ID和状态分页查询提现记录
    Page<Withdrawal> findByUserIdAndStatus(UUID userId, String status, Pageable pageable);
}