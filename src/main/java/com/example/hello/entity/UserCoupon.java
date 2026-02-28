package com.example.hello.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 用户优惠券实体
 */
@Data
@Entity
@Table(name = "user_coupons")
public class UserCoupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;
    
    @Column(name = "coupon_id", nullable = false, length = 36)
    private String couponId;
    
    @Column(name = "coupon_code", nullable = false, length = 50)
    private String couponCode;
    
    @Column(name = "coupon_name", nullable = false, length = 100)
    private String couponName;
    
    @Column(name = "coupon_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Coupon.CouponType couponType;
    
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal discountValue;
    
    @Column(name = "min_order_amount", precision = 10, scale = 2)
    private java.math.BigDecimal minOrderAmount;
    
    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private java.math.BigDecimal maxDiscountAmount;
    
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;
    
    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;
    
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserCouponStatus status = UserCouponStatus.UNUSED;
    
    @Column(name = "claimed_at", nullable = false)
    private LocalDateTime claimedAt;
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    @Column(name = "order_id", length = 36)
    private String orderId; // 使用此优惠券的订单ID
    
    @Column(name = "expired_at")
    private LocalDateTime expiredAt; // 过期时间（用于状态管理）
    
    @PrePersist
    protected void onCreate() {
        if (claimedAt == null) {
            claimedAt = LocalDateTime.now();
        }
    }
    
    /**
     * 用户优惠券状态枚举
     */
    public enum UserCouponStatus {
        UNUSED("未使用"),
        USED("已使用"),
        EXPIRED("已过期");
        
        private final String description;
        
        UserCouponStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
