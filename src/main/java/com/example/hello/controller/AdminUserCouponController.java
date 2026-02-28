package com.example.hello.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.hello.common.ApiResponse;
import com.example.hello.common.RateLimit;
import com.example.hello.entity.UserCoupon;
import com.example.hello.service.CouponService;
import lombok.extern.slf4j.Slf4j;

/**
 * 管理端用户优惠券控制器
 * 提供用户优惠券的管理功能
 */
@RestController
@RequestMapping("/admin/api/user-coupons")
@RateLimit(maxRequests = 200, timeWindow = 60, message = "用户优惠券API调用频率过高，请稍后再试")
@Slf4j
public class AdminUserCouponController {
    
    @Autowired
    private CouponService couponService;
    
    /**
     * 获取用户优惠券列表（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserCoupons(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String timeRange) {
        try {
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            
            // 根据筛选条件获取用户优惠券
            Page<UserCoupon> userCoupons;
            if (status != null && !status.trim().isEmpty()) {
                UserCoupon.UserCouponStatus couponStatus = UserCoupon.UserCouponStatus.valueOf(status);
                userCoupons = couponService.getUserCoupons(null, couponStatus, pageable);
            } else {
                // 获取所有用户优惠券（这里需要扩展服务方法）
                userCoupons = couponService.getAllUserCoupons(pageable);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", userCoupons.getTotalElements());
            result.put("list", userCoupons.getContent());
            result.put("page", page);
            result.put("size", pageSize);
            result.put("totalPages", userCoupons.getTotalPages());
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            log.error("获取用户优惠券列表失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取用户优惠券列表失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取用户优惠券统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserCouponStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总用户优惠券数
            long totalUserCoupons = couponService.getTotalUserCoupons();
            statistics.put("totalUserCoupons", totalUserCoupons);
            
            // 各状态用户优惠券数
            long unusedUserCoupons = couponService.getUserCouponsCountByStatus(UserCoupon.UserCouponStatus.UNUSED);
            long usedUserCoupons = couponService.getUserCouponsCountByStatus(UserCoupon.UserCouponStatus.USED);
            long expiredUserCoupons = couponService.getUserCouponsCountByStatus(UserCoupon.UserCouponStatus.EXPIRED);
            
            statistics.put("unusedUserCoupons", unusedUserCoupons);
            statistics.put("usedUserCoupons", usedUserCoupons);
            statistics.put("expiredUserCoupons", expiredUserCoupons);
            
            return ResponseEntity.ok(ApiResponse.success(statistics));
            
        } catch (Exception e) {
            log.error("获取用户优惠券统计失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取用户优惠券统计失败：" + e.getMessage()));
        }
    }
    
    /**
     * 设置用户优惠券为过期
     */
    @PutMapping("/{userCouponId}/expire")
    public ResponseEntity<ApiResponse<String>> expireUserCoupon(@PathVariable String userCouponId) {
        try {
            couponService.expireUserCoupon(userCouponId);
            return ResponseEntity.ok(ApiResponse.success("设置优惠券过期成功"));
            
        } catch (Exception e) {
            log.error("设置用户优惠券过期失败，ID：{}", userCouponId, e);
            return ResponseEntity.ok(ApiResponse.error("设置优惠券过期失败：" + e.getMessage()));
        }
    }
}
