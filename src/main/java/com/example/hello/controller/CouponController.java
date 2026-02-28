package com.example.hello.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.hello.annotation.AuditLog;
import com.example.hello.common.ApiResponse;
import com.example.hello.common.RateLimit;
import com.example.hello.entity.Coupon;
import com.example.hello.service.CouponService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/api/coupons")
@RateLimit(maxRequests = 200, timeWindow = 60, message = "优惠券API调用频率过高，请稍后再试")
@Slf4j
public class CouponController {
    
    @Autowired
    private CouponService couponService;
    
    // 获取优惠券列表
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getCoupons(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        
        try {
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            Page<Coupon> coupons;
            
            if (status != null && !status.isEmpty()) {
                Boolean isActive = Boolean.parseBoolean(status);
                coupons = couponService.getCouponsByStatus(isActive, pageable);
            } else if (type != null && !type.isEmpty()) {
                Coupon.CouponType couponType = Coupon.CouponType.valueOf(type);
                coupons = couponService.getCouponsByType(couponType, pageable);
            } else if (keyword != null && !keyword.isEmpty()) {
                coupons = couponService.searchCoupons(keyword, pageable);
            } else {
                coupons = couponService.getAllCoupons(pageable);
            }
            
            Map<String, Object> result = Map.of(
                "total", coupons.getTotalElements(),
                "list", coupons.getContent(),
                "page", page,
                "pageSize", pageSize,
                "totalPages", coupons.getTotalPages()
            );
            
            return ApiResponse.success(result);
            
        } catch (Exception e) {
            return ApiResponse.error("获取优惠券列表失败：" + e.getMessage());
        }
    }
    
    // 获取优惠券详情
    @GetMapping("/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Coupon> getCouponDetail(@PathVariable String couponId) {
        try {
            Coupon coupon = couponService.getCouponById(couponId);
            return ApiResponse.success(coupon);
        } catch (Exception e) {
            return ApiResponse.error("获取优惠券详情失败：" + e.getMessage());
        }
    }
    
    // 创建优惠券
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @AuditLog(operation = "CREATE", resourceType = "COUPON", description = "创建优惠券")
    public ApiResponse<Coupon> createCoupon(@RequestBody Coupon coupon) {
        try {
            if (!couponService.validateCouponData(coupon)) {
                return ApiResponse.error("优惠券数据验证失败");
            }
            
            if (!couponService.isCouponCodeAvailable(coupon.getCouponCode(), null)) {
                return ApiResponse.error("优惠券代码已存在");
            }
            
            Coupon createdCoupon = couponService.createCoupon(coupon);
            return ApiResponse.success(createdCoupon);
            
        } catch (Exception e) {
            return ApiResponse.error("创建优惠券失败：" + e.getMessage());
        }
    }
    
    // 更新优惠券
    @PutMapping("/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    @AuditLog(operation = "UPDATE", resourceType = "COUPON", description = "更新优惠券")
    public ApiResponse<Coupon> updateCoupon(@PathVariable String couponId, @RequestBody Coupon coupon) {
        try {
            if (!couponService.validateCouponData(coupon)) {
                return ApiResponse.error("优惠券数据验证失败");
            }
            
            if (!couponService.isCouponCodeAvailable(coupon.getCouponCode(), couponId)) {
                return ApiResponse.error("优惠券代码已存在");
            }
            
            Coupon updatedCoupon = couponService.updateCoupon(couponId, coupon);
            return ApiResponse.success(updatedCoupon);
            
        } catch (Exception e) {
            return ApiResponse.error("更新优惠券失败：" + e.getMessage());
        }
    }
    
    // 删除优惠券
    @DeleteMapping("/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    @AuditLog(operation = "DELETE", resourceType = "COUPON", description = "删除优惠券")
    public ApiResponse<Void> deleteCoupon(@PathVariable String couponId) {
        try {
            couponService.deleteCoupon(couponId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("删除优惠券失败：" + e.getMessage());
        }
    }
    
    // 切换优惠券状态
    @PutMapping("/{couponId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @AuditLog(operation = "UPDATE", resourceType = "COUPON", description = "切换优惠券状态")
    public ApiResponse<Coupon> toggleCouponStatus(@PathVariable String couponId, @RequestParam Boolean isActive) {
        try {
            Coupon updatedCoupon = couponService.toggleCouponStatus(couponId, isActive);
            return ApiResponse.success(updatedCoupon);
        } catch (Exception e) {
            return ApiResponse.error("切换优惠券状态失败：" + e.getMessage());
        }
    }
    
    // 获取优惠券统计信息
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getCouponStatistics() {
        try {
            Map<String, Object> statistics = couponService.getCouponStatistics();
            return ApiResponse.success(statistics);
        } catch (Exception e) {
            return ApiResponse.error("获取优惠券统计信息失败：" + e.getMessage());
        }
    }
    
}