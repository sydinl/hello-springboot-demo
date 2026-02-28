package com.example.hello.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "coupons")
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "coupon_id", length = 36)
    private String id;
    
    @Column(name = "coupon_code", nullable = false, unique = true, length = 50)
    private String couponCode;
    
    @Column(name = "coupon_name", nullable = false, length = 100)
    private String couponName;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_type", nullable = false)
    private CouponType couponType;
    
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;
    
    @Column(name = "min_order_amount", precision = 10, scale = 2)
    private BigDecimal minOrderAmount;
    
    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;
    
    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;
    
    @Column(name = "used_quantity", nullable = false)
    private Integer usedQuantity = 0;
    
    @Column(name = "remaining_quantity", nullable = false)
    private Integer remainingQuantity;
    
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;
    
    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;
    
    @Column(name = "usage_limit_per_user")
    private Integer usageLimitPerUser = 1;
    
    @Column(name = "applicable_categories", length = 1000)
    private String applicableCategories; // 逗号分隔的分类ID
    
    @Column(name = "applicable_projects", length = 1000)
    private String applicableProjects; // 逗号分隔的项目ID
    
    @Column(name = "created_by", length = 50)
    private String createdBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (remainingQuantity == null) {
            remainingQuantity = totalQuantity;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (remainingQuantity == null) {
            remainingQuantity = totalQuantity - usedQuantity;
        }
    }
    
    public enum CouponType {
        PERCENTAGE("百分比折扣"),
        FIXED_AMOUNT("固定金额折扣"),
        FREE_SHIPPING("免运费");
        
        private final String description;
        
        CouponType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}