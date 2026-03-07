package com.example.hello.repository;

import com.example.hello.entity.DistributionOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributionOrderRepository extends JpaRepository<DistributionOrder, String> {
    // 按推荐人ID分页查询分销订单
    Page<DistributionOrder> findByReferrerId(String referrerId, Pageable pageable);
    
    // 按推荐人ID和状态分页查询分销订单
    Page<DistributionOrder> findByReferrerIdAndStatus(String referrerId, String status, Pageable pageable);

    /** 是否已有该订单的分销记录（防重复生成） */
    boolean existsByOrderId(String orderId);
}