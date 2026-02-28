package com.example.hello.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.hello.dto.CreateOrderRequest;
import com.example.hello.dto.WechatPaymentParams;
import com.example.hello.entity.Coupon;
import com.example.hello.entity.Order;
import com.example.hello.service.CouponValidationService.CouponValidationResult;

@Service
public interface OrderService {
    // 创建订单
    Order createOrder(Order order);
    
    // 获取订单详情
    Order getOrderDetail(UUID orderId);
    
    // 按用户ID查询订单列表
    Page<Order> getOrderListByUserId(String userId, Pageable pageable);
    
    // 按用户ID和状态查询订单列表
    Page<Order> getOrderListByUserIdAndStatus(String userId, String status, Pageable pageable);
    
    // 更新订单状态
    Order updateOrderStatus(UUID orderId, String status);
    
    // ========== 管理员订单管理接口 ==========
    
    // 获取所有订单（管理员用）
    Page<Order> getAllOrders(String status, String userId, String search, Pageable pageable);
    
    // 获取订单统计信息
    Map<String, Object> getOrderStatistics();
    
    // ========== 支付相关接口 ==========
    
    // 通过CreateOrderRequest创建订单
    Order createOrderFromRequest(CreateOrderRequest request, String userId);
    
    // 根据订单号获取订单详情
    Order getOrderByOrderNo(String orderNo);
    
    // 获取微信支付参数
    WechatPaymentParams getWechatPaymentParams(UUID orderId);
    
    // 查询支付状态
    String queryPaymentStatus(UUID orderId);
    
    // 更新订单状态（支付相关）
    void updateOrderStatus(String orderNo, String status, String transactionId);
    
    // 处理支付回调
    boolean handlePaymentCallback(String callbackData);
    
    // ========== 优惠券相关接口 ==========
    
    // 验证优惠券
    CouponValidationResult validateCoupon(String couponCode, String userId, BigDecimal orderAmount, List<String> projectIds);
    
    // 获取可用优惠券列表
    List<Coupon> getAvailableCoupons(String userId, BigDecimal orderAmount, List<String> projectIds);
    
    // 计算订单最终金额（包含优惠券）
    BigDecimal calculateFinalAmount(BigDecimal originalAmount, String couponCode, String userId, List<String> projectIds);
}