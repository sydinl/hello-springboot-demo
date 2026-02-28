package com.example.hello.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.hello.entity.Coupon;
import com.example.hello.repository.CouponRepository;
import com.example.hello.repository.OrderCouponUsageRepository;
import com.example.hello.service.CouponValidationService;
import lombok.extern.slf4j.Slf4j;

/**
 * 优惠券验证和计算服务实现
 */
@Service
@Slf4j
public class CouponValidationServiceImpl implements CouponValidationService {
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Autowired
    private OrderCouponUsageRepository orderCouponUsageRepository;
    
    @Override
    public CouponValidationResult validateCoupon(String couponCode, String userId, BigDecimal orderAmount, List<String> projectIds) {
        try {
            // 1. 查找优惠券
            Optional<Coupon> couponOpt = couponRepository.findValidCoupon(couponCode, LocalDateTime.now());
            if (couponOpt.isEmpty()) {
                return new CouponValidationResult(false, "优惠券不存在或已失效");
            }
            
            Coupon coupon = couponOpt.get();
            
            // 2. 检查最低订单金额
            if (coupon.getMinOrderAmount() != null && orderAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
                return new CouponValidationResult(false, 
                    String.format("订单金额需满%.2f元才能使用此优惠券", coupon.getMinOrderAmount().doubleValue()));
            }
            
            // 3. 检查用户使用次数限制
            if (coupon.getUsageLimitPerUser() != null) {
                long usedCount = orderCouponUsageRepository.countByUserIdAndCouponId(userId, coupon.getId());
                if (usedCount >= coupon.getUsageLimitPerUser()) {
                    return new CouponValidationResult(false, "您已达到此优惠券的使用次数限制");
                }
            }
            
            // 4. 检查适用范围
            if (!isCouponApplicable(coupon, projectIds)) {
                return new CouponValidationResult(false, "此优惠券不适用于当前订单中的项目");
            }
            
            // 5. 计算优惠金额
            BigDecimal discountAmount = calculateDiscountAmount(coupon, orderAmount);
            
            return new CouponValidationResult(true, "优惠券验证成功", coupon, discountAmount);
            
        } catch (Exception e) {
            log.error("验证优惠券失败", e);
            return new CouponValidationResult(false, "优惠券验证失败：" + e.getMessage());
        }
    }
    
    @Override
    public BigDecimal calculateDiscountAmount(Coupon coupon, BigDecimal orderAmount) {
        BigDecimal discountAmount = BigDecimal.ZERO;
        
        switch (coupon.getCouponType()) {
            case PERCENTAGE:
                // 百分比折扣
                discountAmount = orderAmount.multiply(coupon.getDiscountValue()).divide(new BigDecimal("100"));
                break;
            case FIXED_AMOUNT:
                // 固定金额折扣
                discountAmount = coupon.getDiscountValue();
                break;
            case FREE_SHIPPING:
                // 免运费（这里假设运费为0，实际项目中可能需要单独计算运费）
                discountAmount = BigDecimal.ZERO;
                break;
        }
        
        // 应用最大折扣金额限制
        if (coupon.getMaxDiscountAmount() != null && discountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
            discountAmount = coupon.getMaxDiscountAmount();
        }
        
        // 确保优惠金额不超过订单金额
        if (discountAmount.compareTo(orderAmount) > 0) {
            discountAmount = orderAmount;
        }
        
        return discountAmount;
    }
    
    @Override
    public List<Coupon> getAvailableCoupons(String userId, BigDecimal orderAmount, List<String> projectIds) {
        try {
            List<Coupon> availableCoupons = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            
            // 查找所有有效的公开优惠券
            List<Coupon> allCoupons = couponRepository.findAll().stream()
                .filter(coupon -> coupon.getIsActive() && coupon.getIsPublic())
                .filter(coupon -> coupon.getValidFrom().isBefore(now) && coupon.getValidUntil().isAfter(now))
                .filter(coupon -> coupon.getRemainingQuantity() > 0)
                .toList();
            
            for (Coupon coupon : allCoupons) {
                CouponValidationResult result = validateCoupon(coupon.getCouponCode(), userId, orderAmount, projectIds);
                if (result.isValid()) {
                    availableCoupons.add(coupon);
                }
            }
            
            return availableCoupons;
            
        } catch (Exception e) {
            log.error("获取可用优惠券失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 检查优惠券是否适用于指定项目
     */
    private boolean isCouponApplicable(Coupon coupon, List<String> projectIds) {
        // 如果没有设置适用范围，则适用于所有项目
        if ((coupon.getApplicableCategories() == null || coupon.getApplicableCategories().isEmpty()) &&
            (coupon.getApplicableProjects() == null || coupon.getApplicableProjects().isEmpty())) {
            return true;
        }
        
        // 检查项目适用范围
        if (coupon.getApplicableProjects() != null && !coupon.getApplicableProjects().isEmpty()) {
            List<String> applicableProjects = Arrays.asList(coupon.getApplicableProjects().split(","));
            for (String projectId : projectIds) {
                if (applicableProjects.contains(projectId)) {
                    return true;
                }
            }
        }
        
        // 这里可以添加分类适用范围的检查逻辑
        // 需要根据项目ID查询项目分类，然后检查是否在适用分类中
        
        return false;
    }
}

