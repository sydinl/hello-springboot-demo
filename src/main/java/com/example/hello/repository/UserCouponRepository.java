package com.example.hello.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.hello.entity.UserCoupon;
import com.example.hello.entity.UserCoupon.UserCouponStatus;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, String> {
    
    // 根据用户ID查找优惠券
    Page<UserCoupon> findByUserId(String userId, Pageable pageable);
    
    // 根据用户ID和状态查找优惠券
    Page<UserCoupon> findByUserIdAndStatus(String userId, UserCouponStatus status, Pageable pageable);
    
    // 根据用户ID和优惠券ID查找
    Optional<UserCoupon> findByUserIdAndCouponId(String userId, String couponId);
    
    // 根据用户ID和优惠券代码查找
    Optional<UserCoupon> findByUserIdAndCouponCode(String userId, String couponCode);
    
    // 查找用户可用的优惠券（未使用且未过期）
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.userId = :userId AND uc.status = 'UNUSED' AND uc.validUntil >= :now ORDER BY uc.validUntil ASC")
    List<UserCoupon> findAvailableCouponsByUserId(@Param("userId") String userId, @Param("now") LocalDateTime now);
    
    // 查找用户即将过期的优惠券
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.userId = :userId AND uc.status = 'UNUSED' AND uc.validUntil BETWEEN :startDate AND :endDate")
    List<UserCoupon> findExpiringCouponsByUserId(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // 统计用户优惠券数量
    long countByUserId(String userId);
    
    // 统计用户各状态优惠券数量
    long countByUserIdAndStatus(String userId, UserCouponStatus status);
    
    // 统计各状态优惠券数量（不限制用户）
    long countByStatus(UserCouponStatus status);
    
    // 统计用户领取特定优惠券的次数
    long countByUserIdAndCouponId(String userId, String couponId);
    
    // 查找已过期的优惠券
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.status = 'UNUSED' AND uc.validUntil < :now")
    List<UserCoupon> findExpiredCoupons(@Param("now") LocalDateTime now);
    
    // 根据订单ID查找使用的优惠券
    Optional<UserCoupon> findByOrderId(String orderId);
}
