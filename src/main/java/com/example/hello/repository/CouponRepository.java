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
import com.example.hello.entity.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
    
    // 根据优惠券代码查找
    Optional<Coupon> findByCouponCode(String couponCode);
    
    // 根据优惠券代码查找（忽略大小写）
    Optional<Coupon> findByCouponCodeIgnoreCase(String couponCode);
    
    // 查找有效的优惠券
    @Query("SELECT c FROM Coupon c WHERE c.couponCode = :couponCode AND c.isActive = true AND c.validFrom <= :now AND c.validUntil >= :now AND c.remainingQuantity > 0")
    Optional<Coupon> findValidCoupon(@Param("couponCode") String couponCode, @Param("now") LocalDateTime now);
    
    // 根据状态查找优惠券
    Page<Coupon> findByIsActive(Boolean isActive, Pageable pageable);
    
    // 根据公开状态查找优惠券
    Page<Coupon> findByIsPublic(Boolean isPublic, Pageable pageable);
    
    // 根据优惠券类型查找
    Page<Coupon> findByCouponType(Coupon.CouponType couponType, Pageable pageable);
    
    // 根据创建者查找
    Page<Coupon> findByCreatedBy(String createdBy, Pageable pageable);
    
    // 查找即将过期的优惠券
    @Query("SELECT c FROM Coupon c WHERE c.validUntil BETWEEN :startDate AND :endDate AND c.isActive = true")
    Page<Coupon> findExpiringCoupons(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 查找已过期的优惠券
    @Query("SELECT c FROM Coupon c WHERE c.validUntil < :now AND c.isActive = true")
    Page<Coupon> findExpiredCoupons(@Param("now") LocalDateTime now, Pageable pageable);
    
    // 根据名称模糊查询
    Page<Coupon> findByCouponNameContaining(String keyword, Pageable pageable);
    
    // 根据代码模糊查询
    Page<Coupon> findByCouponCodeContaining(String keyword, Pageable pageable);
    
    // 统计优惠券总数
    @Query("SELECT COUNT(c) FROM Coupon c")
    Long countAllCoupons();
    
    // 统计有效优惠券数量
    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.isActive = true AND c.validFrom <= :now AND c.validUntil >= :now")
    Long countValidCoupons(@Param("now") LocalDateTime now);
    
    // 统计已使用优惠券数量
    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.usedQuantity > 0")
    Long countUsedCoupons();
    
    // 统计按类型分组的优惠券数量
    @Query("SELECT c.couponType, COUNT(c) FROM Coupon c GROUP BY c.couponType")
    Object[][] countCouponsByType();
    
    // 统计按状态分组的优惠券数量
    @Query("SELECT c.isActive, COUNT(c) FROM Coupon c GROUP BY c.isActive")
    Object[][] countCouponsByStatus();
    
    // 查找用户可用的优惠券
    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND c.isPublic = true AND c.validFrom <= :now AND c.validUntil >= :now AND c.remainingQuantity > 0")
    List<Coupon> findAvailableCoupons(@Param("now") LocalDateTime now);
    
    // 根据分类查找可用优惠券
    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND c.isPublic = true AND c.validFrom <= :now AND c.validUntil >= :now AND c.remainingQuantity > 0 AND (c.applicableCategories IS NULL OR c.applicableCategories LIKE %:categoryId%)")
    List<Coupon> findAvailableCouponsByCategory(@Param("now") LocalDateTime now, @Param("categoryId") String categoryId);
    
    // 根据项目查找可用优惠券
    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND c.isPublic = true AND c.validFrom <= :now AND c.validUntil >= :now AND c.remainingQuantity > 0 AND (c.applicableProjects IS NULL OR c.applicableProjects LIKE %:projectId%)")
    List<Coupon> findAvailableCouponsByProject(@Param("now") LocalDateTime now, @Param("projectId") String projectId);
}