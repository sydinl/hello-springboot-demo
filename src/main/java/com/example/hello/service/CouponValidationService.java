package com.example.hello.service;

import java.math.BigDecimal;
import java.util.List;
import com.example.hello.entity.Coupon;

/**
 * 优惠券验证和计算服务接口
 */
public interface CouponValidationService {
    
    /**
     * 验证优惠券是否可用
     * @param couponCode 优惠券代码
     * @param userId 用户ID
     * @param orderAmount 订单金额
     * @param projectIds 项目ID列表
     * @return 验证结果
     */
    CouponValidationResult validateCoupon(String couponCode, String userId, BigDecimal orderAmount, List<String> projectIds);
    
    /**
     * 计算优惠金额
     * @param coupon 优惠券
     * @param orderAmount 订单金额
     * @return 优惠金额
     */
    BigDecimal calculateDiscountAmount(Coupon coupon, BigDecimal orderAmount);
    
    /**
     * 获取用户可用的优惠券列表
     * @param userId 用户ID
     * @param orderAmount 订单金额
     * @param projectIds 项目ID列表
     * @return 可用优惠券列表
     */
    List<Coupon> getAvailableCoupons(String userId, BigDecimal orderAmount, List<String> projectIds);
    
    /**
     * 优惠券验证结果
     */
    class CouponValidationResult {
        private boolean valid;
        private String message;
        private Coupon coupon;
        private BigDecimal discountAmount;
        
        public CouponValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public CouponValidationResult(boolean valid, String message, Coupon coupon, BigDecimal discountAmount) {
            this.valid = valid;
            this.message = message;
            this.coupon = coupon;
            this.discountAmount = discountAmount;
        }
        
        // Getters and Setters
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public Coupon getCoupon() {
            return coupon;
        }
        
        public void setCoupon(Coupon coupon) {
            this.coupon = coupon;
        }
        
        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }
        
        public void setDiscountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
        }
    }
}

