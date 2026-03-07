package com.example.hello.repository;

import com.example.hello.entity.DistributionOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DistributionOrderRepository extends JpaRepository<DistributionOrder, String> {
    // 按推荐人ID分页查询分销订单
    Page<DistributionOrder> findByReferrerId(String referrerId, Pageable pageable);
    
    // 按推荐人ID和状态分页查询分销订单
    Page<DistributionOrder> findByReferrerIdAndStatus(String referrerId, String status, Pageable pageable);

    /** 是否已有该订单的分销记录（防重复生成） */
    boolean existsByOrderId(String orderId);

    /** 某推荐人的佣金总和 */
    @Query("SELECT COALESCE(SUM(d.commission), 0) FROM DistributionOrder d WHERE d.referrerId = :referrerId")
    Double sumCommissionByReferrerId(@Param("referrerId") String referrerId);

    /** 某推荐人指定状态的佣金总和 */
    @Query("SELECT COALESCE(SUM(d.commission), 0) FROM DistributionOrder d WHERE d.referrerId = :referrerId AND d.status = :status")
    Double sumCommissionByReferrerIdAndStatus(@Param("referrerId") String referrerId, @Param("status") String status);

    /** 某推荐人在指定时间之后产生的分销订单数（用于今日订单数） */
    long countByReferrerIdAndCreateTimeAfter(String referrerId, Date createTime);

    /** 某推荐人的分销订单总数 */
    long countByReferrerId(String referrerId);

    /** 按推荐人汇总佣金并倒序，用于排行（前 limit 条） */
    @Query("SELECT d.referrerId, SUM(d.commission) FROM DistributionOrder d GROUP BY d.referrerId ORDER BY SUM(d.commission) DESC")
    List<Object[]> sumCommissionGroupByReferrerId(Pageable pageable);
}