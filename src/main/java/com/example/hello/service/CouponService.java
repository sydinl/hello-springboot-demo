package com.example.hello.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.hello.entity.Coupon;
import com.example.hello.entity.UserCoupon;

public interface CouponService {
    
    // ========== 基础CRUD操作 ==========
    
    // 创建优惠券
    Coupon createCoupon(Coupon coupon);
    
    // 更新优惠券
    Coupon updateCoupon(String couponId, Coupon coupon);
    
    // 删除优惠券
    void deleteCoupon(String couponId);
    
    // 批量删除优惠券
    void deleteCoupons(String[] couponIds);
    
    // 根据ID获取优惠券
    Coupon getCouponById(String couponId);
    
    // 根据代码获取优惠券
    Coupon getCouponByCode(String couponCode);
    
    // 获取所有优惠券（分页）
    Page<Coupon> getAllCoupons(Pageable pageable);
    
    // ========== 查询操作 ==========
    
    // 根据状态查询优惠券
    Page<Coupon> getCouponsByStatus(Boolean isActive, Pageable pageable);
    
    // 根据类型查询优惠券
    Page<Coupon> getCouponsByType(Coupon.CouponType couponType, Pageable pageable);
    
    // 根据创建者查询优惠券
    Page<Coupon> getCouponsByCreator(String createdBy, Pageable pageable);
    
    // 搜索优惠券
    Page<Coupon> searchCoupons(String keyword, Pageable pageable);
    
    // 查找即将过期的优惠券
    Page<Coupon> getExpiringCoupons(int days, Pageable pageable);
    
    // 查找已过期的优惠券
    Page<Coupon> getExpiredCoupons(Pageable pageable);
    
    // ========== 状态管理 ==========
    
    // 启用/禁用优惠券
    Coupon toggleCouponStatus(String couponId, Boolean isActive);
    
    // 设置优惠券公开状态
    Coupon setCouponPublicStatus(String couponId, Boolean isPublic);
    
    // ========== 使用管理 ==========
    
    // 使用优惠券
    Coupon useCoupon(String couponCode, String userId);
    
    // 验证优惠券是否可用
    boolean isCouponValid(String couponCode, String userId, BigDecimal orderAmount);
    
    // 计算优惠金额
    BigDecimal calculateDiscount(String couponCode, BigDecimal orderAmount);
    
    // 获取用户可用的优惠券
    List<Coupon> getAvailableCouponsForUser(String userId);
    
    // 根据分类获取可用优惠券
    List<Coupon> getAvailableCouponsByCategory(String categoryId);
    
    // 根据项目获取可用优惠券
    List<Coupon> getAvailableCouponsByProject(String projectId);
    
    // ========== 统计信息 ==========
    
    // 获取优惠券统计信息
    Map<String, Object> getCouponStatistics();
    
    // 获取优惠券使用统计
    Map<String, Object> getCouponUsageStatistics();
    
    // ========== 批量操作 ==========
    
    // 批量更新优惠券状态
    int batchUpdateCouponStatus(List<String> couponIds, Boolean isActive);
    
    // 批量删除优惠券
    int batchDeleteCoupons(List<String> couponIds);
    
    // ========== 验证操作 ==========
    
    // 检查优惠券代码是否可用
    boolean isCouponCodeAvailable(String couponCode, String excludeCouponId);
    
    // 验证优惠券数据
    boolean validateCouponData(Coupon coupon);
    
    // ========== 用户优惠券管理 ==========
    
    // 用户领取优惠券
    UserCoupon claimCoupon(String couponCode, String userId);
    
    // 获取用户的优惠券列表
    Page<UserCoupon> getUserCoupons(String userId, UserCoupon.UserCouponStatus status, Pageable pageable);
    
    // 获取用户可用的优惠券
    List<UserCoupon> getAvailableUserCoupons(String userId);
    
    // 使用用户优惠券
    UserCoupon useUserCoupon(String userCouponId, String orderId);
    
    // 获取用户优惠券统计
    Map<String, Object> getUserCouponStatistics(String userId);
    
    // ========== 管理端用户优惠券管理 ==========
    
    // 获取所有用户优惠券（分页）
    Page<UserCoupon> getAllUserCoupons(Pageable pageable);
    
    // 获取用户优惠券总数
    long getTotalUserCoupons();
    
    // 根据状态获取用户优惠券数量
    long getUserCouponsCountByStatus(UserCoupon.UserCouponStatus status);
    
    // 设置用户优惠券为过期
    void expireUserCoupon(String userCouponId);
}