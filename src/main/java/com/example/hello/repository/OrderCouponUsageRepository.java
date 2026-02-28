package com.example.hello.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.hello.entity.OrderCouponUsage;

@Repository
public interface OrderCouponUsageRepository extends JpaRepository<OrderCouponUsage, String> {
    
    // 根据订单ID查找使用记录
    List<OrderCouponUsage> findByOrderId(String orderId);
    
    // 根据用户ID查找使用记录
    List<OrderCouponUsage> findByUserId(String userId);
    
    // 根据优惠券ID查找使用记录
    List<OrderCouponUsage> findByCouponId(String couponId);
    
    // 根据订单ID和优惠券ID查找使用记录
    Optional<OrderCouponUsage> findByOrderIdAndCouponId(String orderId, String couponId);
    
    // 统计用户使用优惠券次数
    @Query("SELECT COUNT(ocu) FROM OrderCouponUsage ocu WHERE ocu.userId = :userId AND ocu.couponId = :couponId")
    long countByUserIdAndCouponId(@Param("userId") String userId, @Param("couponId") String couponId);
    
    // 统计优惠券总使用次数
    @Query("SELECT COUNT(ocu) FROM OrderCouponUsage ocu WHERE ocu.couponId = :couponId")
    long countByCouponId(@Param("couponId") String couponId);
}

