package com.example.hello.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.hello.common.ApiResponse;
import com.example.hello.common.RateLimit;
import com.example.hello.entity.Coupon;
import com.example.hello.entity.UserCoupon;
import com.example.hello.service.CouponService;
import com.example.hello.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户优惠券控制器
 * 处理用户相关的优惠券操作，不需要admin权限
 */
@RestController
@RequestMapping("/api/coupons")
@RateLimit(maxRequests = 200, timeWindow = 60, message = "优惠券API调用频率过高，请稍后再试")
@Slf4j
public class UserCouponController {
    
    @Autowired
    private CouponService couponService;
    
    @Autowired
    private TokenUtil tokenUtil;
    
    // ========== 公开接口（不需要认证，适合微信小程序） ==========
    
    // 公开领取优惠券
    @PostMapping("/public/claim")
    @RateLimit(maxRequests = 20, timeWindow = 60, perUser = true, message = "优惠券领取过于频繁，请稍后再试")
    public ResponseEntity<ApiResponse<UserCoupon>> claimCouponPublic(@RequestBody Map<String, String> request) {
        try {
            String couponCode = request.get("couponCode");
            String userId = request.get("userId");
            
            if (couponCode == null || couponCode.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(1001, "优惠券代码不能为空"));
            }
            
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(1001, "用户ID不能为空"));
            }
            
            UserCoupon userCoupon = couponService.claimCoupon(couponCode, userId);
            return ResponseEntity.ok(ApiResponse.success(userCoupon));
            
        } catch (Exception e) {
            log.error("公开接口用户领取优惠券失败", e);
            return ResponseEntity.ok(ApiResponse.error("领取优惠券失败：" + e.getMessage()));
        }
    }
    
    // 公开获取用户优惠券列表
    @GetMapping("/public/user/list")
    @RateLimit(maxRequests = 200, timeWindow = 60, message = "优惠券查询过于频繁，请稍后再试")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserCouponsPublic(
            @RequestParam String userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(1001, "用户ID不能为空"));
            }
            
            Pageable pageable = PageRequest.of(page - 1, size);
            UserCoupon.UserCouponStatus couponStatus = null;
            if (status != null && !status.trim().isEmpty()) {
                try {
                    couponStatus = UserCoupon.UserCouponStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.ok(ApiResponse.error(1001, "无效的状态参数"));
                }
            }
            
            Page<UserCoupon> userCoupons = couponService.getUserCoupons(userId, couponStatus, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", userCoupons.getTotalElements());
            result.put("list", userCoupons.getContent());
            result.put("page", page);
            result.put("size", size);
            result.put("totalPages", userCoupons.getTotalPages());
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            log.error("公开接口获取用户优惠券列表失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取用户优惠券列表失败：" + e.getMessage()));
        }
    }
    
    // 公开获取用户可用优惠券
    @GetMapping("/public/user/available")
    @RateLimit(maxRequests = 200, timeWindow = 60, message = "优惠券查询过于频繁，请稍后再试")
    public ResponseEntity<ApiResponse<List<UserCoupon>>> getAvailableUserCouponsPublic(@RequestParam String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(1001, "用户ID不能为空"));
            }
            
            List<UserCoupon> availableCoupons = couponService.getAvailableUserCoupons(userId);
            return ResponseEntity.ok(ApiResponse.success(availableCoupons));
            
        } catch (Exception e) {
            log.error("公开接口获取用户可用优惠券失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取用户可用优惠券失败：" + e.getMessage()));
        }
    }
    
    // 公开获取可领取优惠券列表
    @GetMapping("/public/available")
    @RateLimit(maxRequests = 200, timeWindow = 60, message = "优惠券查询过于频繁，请稍后再试")
    public ResponseEntity<ApiResponse<List<Coupon>>> getAvailableCouponsPublic() {
        try {
            List<Coupon> availableCoupons = couponService.getAvailableCouponsForUser(null);
            return ResponseEntity.ok(ApiResponse.success(availableCoupons));
            
        } catch (Exception e) {
            log.error("公开接口获取可领取优惠券列表失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取可领取优惠券列表失败：" + e.getMessage()));
        }
    }
    
    // 验证优惠券
    @PostMapping("/validate")
    @RateLimit(maxRequests = 200, timeWindow = 60, message = "优惠券验证过于频繁，请稍后再试")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateCoupon(@RequestBody Map<String, Object> request) {
        try {
            String couponCode = request.get("couponCode").toString();
            String userId = request.get("userId").toString();
            Double orderAmount = Double.parseDouble(request.get("orderAmount").toString());
            
            boolean isValid = couponService.isCouponValid(couponCode, userId, BigDecimal.valueOf(orderAmount));
            BigDecimal discount = BigDecimal.ZERO;
            
            if (isValid) {
                discount = couponService.calculateDiscount(couponCode, BigDecimal.valueOf(orderAmount));
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("isValid", isValid);
            result.put("discount", discount);
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            log.error("验证优惠券失败", e);
            return ResponseEntity.ok(ApiResponse.error("验证优惠券失败：" + e.getMessage()));
        }
    }
    
    // ========== 用户接口（需要认证，适合有Token的场景） ==========
    
    // 用户领取优惠券
    @PostMapping("/user/claim")
    @RateLimit(maxRequests = 20, timeWindow = 60, perUser = true, message = "优惠券领取过于频繁，请稍后再试")
    public ResponseEntity<ApiResponse<UserCoupon>> claimCoupon(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> request) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "认证失败，请重新登录"));
            }
            
            String couponCode = request.get("couponCode");
            if (couponCode == null || couponCode.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(1001, "优惠券代码不能为空"));
            }
            
            UserCoupon userCoupon = couponService.claimCoupon(couponCode, userId);
            return ResponseEntity.ok(ApiResponse.success(userCoupon));
            
        } catch (Exception e) {
            log.error("用户领取优惠券失败", e);
            return ResponseEntity.ok(ApiResponse.error("领取优惠券失败：" + e.getMessage()));
        }
    }
    
    // 获取用户的优惠券列表
    @GetMapping("/user/list")
    @RateLimit(maxRequests = 200, timeWindow = 60, message = "优惠券查询过于频繁，请稍后再试")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserCoupons(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "认证失败，请重新登录"));
            }
            
            Pageable pageable = PageRequest.of(page - 1, size);
            UserCoupon.UserCouponStatus couponStatus = null;
            if (status != null && !status.trim().isEmpty()) {
                try {
                    couponStatus = UserCoupon.UserCouponStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.ok(ApiResponse.error(1001, "无效的状态参数"));
                }
            }
            
            Page<UserCoupon> userCoupons = couponService.getUserCoupons(userId, couponStatus, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", userCoupons.getTotalElements());
            result.put("list", userCoupons.getContent());
            result.put("page", page);
            result.put("size", size);
            result.put("totalPages", userCoupons.getTotalPages());
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            log.error("获取用户优惠券列表失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取用户优惠券列表失败：" + e.getMessage()));
        }
    }
    
    // 获取用户可用的优惠券
    @GetMapping("/user/available")
    @RateLimit(maxRequests = 200, timeWindow = 60, message = "优惠券查询过于频繁，请稍后再试")
    public ResponseEntity<ApiResponse<List<UserCoupon>>> getAvailableUserCoupons(@RequestHeader("Authorization") String authorization) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "认证失败，请重新登录"));
            }
            
            List<UserCoupon> availableCoupons = couponService.getAvailableUserCoupons(userId);
            return ResponseEntity.ok(ApiResponse.success(availableCoupons));
            
        } catch (Exception e) {
            log.error("获取用户可用优惠券失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取用户可用优惠券失败：" + e.getMessage()));
        }
    }
    
    // 使用用户优惠券
    @PostMapping("/user/use")
    @RateLimit(maxRequests = 20, timeWindow = 60, perUser = true, message = "优惠券使用过于频繁，请稍后再试")
    public ResponseEntity<ApiResponse<UserCoupon>> useUserCoupon(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> request) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "认证失败，请重新登录"));
            }
            
            String userCouponId = request.get("userCouponId");
            String orderId = request.get("orderId");
            
            if (userCouponId == null || userCouponId.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(1001, "用户优惠券ID不能为空"));
            }
            
            if (orderId == null || orderId.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(1001, "订单ID不能为空"));
            }
            
            UserCoupon userCoupon = couponService.useUserCoupon(userCouponId, orderId);
            return ResponseEntity.ok(ApiResponse.success(userCoupon));
            
        } catch (Exception e) {
            log.error("使用用户优惠券失败", e);
            return ResponseEntity.ok(ApiResponse.error("使用用户优惠券失败：" + e.getMessage()));
        }
    }
    
    // 获取用户优惠券统计
    @GetMapping("/user/statistics")
    @RateLimit(maxRequests = 100, timeWindow = 60, message = "优惠券统计查询过于频繁，请稍后再试")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserCouponStatistics(@RequestHeader("Authorization") String authorization) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "认证失败，请重新登录"));
            }
            
            Map<String, Object> statistics = couponService.getUserCouponStatistics(userId);
            return ResponseEntity.ok(ApiResponse.success(statistics));
            
        } catch (Exception e) {
            log.error("获取用户优惠券统计失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取用户优惠券统计失败：" + e.getMessage()));
        }
    }
}
