package com.example.hello.repository;

import com.example.hello.entity.DistributionOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DistributionOrderRepository extends JpaRepository<DistributionOrder, UUID> {
    // 按推荐人ID分页查询分销订单
    Page<DistributionOrder> findByReferrerId(UUID referrerId, Pageable pageable);
    
    // 按推荐人ID和状态分页查询分销订单
    Page<DistributionOrder> findByReferrerIdAndStatus(UUID referrerId, String status, Pageable pageable);
}