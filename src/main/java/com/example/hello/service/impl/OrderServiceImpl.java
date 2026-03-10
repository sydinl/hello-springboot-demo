package com.example.hello.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.hello.dto.CreateOrderRequest;
import com.example.hello.dto.WechatPaymentParams;
import com.example.hello.entity.Coupon;
import com.example.hello.entity.Order;
import com.example.hello.entity.OrderCouponUsage;
import com.example.hello.entity.OrderItem;
import com.example.hello.entity.User;
import com.example.hello.repository.OrderCouponUsageRepository;
import com.example.hello.repository.OrderItemRepository;
import com.example.hello.repository.OrderRepository;
import com.example.hello.repository.UserRepository;
import com.example.hello.service.CouponValidationService;
import com.example.hello.service.WechatPayService;
import com.example.hello.service.CouponValidationService.CouponValidationResult;
import com.example.hello.service.OrderService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CouponValidationService couponValidationService;
    
    @Autowired
    private OrderCouponUsageRepository orderCouponUsageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WechatPayService wechatPayService;
    
    @Override
    @Transactional
    public Order createOrder(Order order) {
        try {
            // 生成订单ID
            if (order.getOrderId() == null) {
                order.setOrderId(UUID.randomUUID().toString());
            }
            
            // 保存订单到数据库
            Order savedOrder = orderRepository.save(order);
            log.info("创建订单成功，订单ID：{}", savedOrder.getOrderId());
            return savedOrder;
            
        } catch (Exception e) {
            log.error("创建订单失败", e);
            throw new RuntimeException("创建订单失败", e);
        }
    }
    
    @Override
    public Order getOrderDetail(UUID orderId) {
        try {
            return orderRepository.findByIdWithItems(orderId.toString())
                    .orElseThrow(() -> new RuntimeException("订单不存在，订单ID：" + orderId));
        } catch (Exception e) {
            log.error("查询订单详情失败，订单ID：{}", orderId, e);
            throw new RuntimeException("查询订单详情失败", e);
        }
    }
    
    @Override
    public Page<Order> getOrderListByUserId(String userId, Pageable pageable) {
        try {
            log.info("查询用户订单列表，用户ID：{}，分页参数：page={}, size={}", userId, pageable.getPageNumber(), pageable.getPageSize());
            Page<Order> result = orderRepository.findByUserId(userId, pageable);
            log.info("查询结果：总数={}, 当前页内容数量={}", result.getTotalElements(), result.getContent().size());
            return result;
        } catch (Exception e) {
            log.error("查询用户订单列表失败，用户ID：{}", userId, e);
            throw new RuntimeException("查询订单列表失败", e);
        }
    }
    
    @Override
    public Page<Order> getOrderListByUserIdAndStatus(String userId, String status, Pageable pageable) {
        try {
            log.info("查询用户指定状态订单列表，用户ID：{}，状态：{}，分页参数：page={}, size={}", 
                    userId, status, pageable.getPageNumber(), pageable.getPageSize());
            
            // 先查询总数，验证状态值是否正确
            long totalCount = orderRepository.countByUserIdAndStatus(userId, status);
            log.info("用户{}状态{}的订单总数：{}", userId, status, totalCount);
            
            // 如果总数为0，直接返回空结果
            if (totalCount == 0) {
                log.info("用户{}没有状态为{}的订单", userId, status);
                return orderRepository.findByUserIdAndStatus(userId, status, pageable);
            }
            
            Page<Order> result = orderRepository.findByUserIdAndStatus(userId, status, pageable);
            
            log.info("查询结果：总数={}, 当前页内容数量={}, 总页数={}, 当前页号={}", 
                    result.getTotalElements(), result.getContent().size(), 
                    result.getTotalPages(), result.getNumber());
            
            // 如果总数大于0但当前页内容为空，记录详细信息
            if (result.getTotalElements() > 0 && result.getContent().isEmpty()) {
                log.warn("发现分页异常：总数={}但当前页内容为空，当前页号={}, 总页数={}", 
                        result.getTotalElements(), result.getNumber(), result.getTotalPages());
                
                // 尝试查询第一页数据来验证
                Pageable firstPage = PageRequest.of(0, pageable.getPageSize());
                Page<Order> firstPageResult = orderRepository.findByUserIdAndStatus(userId, status, firstPage);
                log.warn("第一页查询结果：总数={}, 内容数量={}", 
                        firstPageResult.getTotalElements(), firstPageResult.getContent().size());
            }
            
            return result;
        } catch (Exception e) {
            log.error("查询用户指定状态订单列表失败，用户ID：{}，状态：{}", userId, status, e);
            throw new RuntimeException("查询订单列表失败", e);
        }
    }
    
    @Override
    @Transactional
    public Order updateOrderStatus(UUID orderId, String status) {
        try {
            Order order = orderRepository.findById(orderId.toString())
                    .orElseThrow(() -> new RuntimeException("订单不存在，订单ID：" + orderId));
            
            order.setStatus(status);
            Order updatedOrder = orderRepository.save(order);
            
            log.info("更新订单状态成功，订单ID：{}，新状态：{}", orderId, status);
            return updatedOrder;
            
        } catch (Exception e) {
            log.error("更新订单状态失败，订单ID：{}，状态：{}", orderId, status, e);
            throw new RuntimeException("更新订单状态失败", e);
        }
    }
    
    // ========== 管理员订单管理接口实现 ==========
    
    @Override
    public Page<Order> getAllOrders(String status, String userId, String search, Pageable pageable) {
        try {
            if (status != null && !status.isEmpty()) {
                return orderRepository.findByStatus(status, pageable);
            } else if (userId != null && !userId.isEmpty()) {
                return orderRepository.findByUserId(userId, pageable);
            } else if (search != null && !search.isEmpty()) {
                return orderRepository.findByOrderIdContaining(search, pageable);
            } else {
                return orderRepository.findAll(pageable);
            }
        } catch (Exception e) {
            log.error("查询所有订单失败，状态：{}，用户ID：{}，搜索：{}", status, userId, search, e);
            throw new RuntimeException("查询订单列表失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getOrderStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总订单数
            long totalOrders = orderRepository.count();
            statistics.put("totalOrders", totalOrders);
            
            // 各状态订单数
            long pendingOrders = orderRepository.countByStatus("pending");
            long shippingOrders = orderRepository.countByStatus("shipping");
            long completedOrders = orderRepository.countByStatus("completed");
            long aftersaleOrders = orderRepository.countByStatus("aftersale");
            
            statistics.put("pendingOrders", pendingOrders);
            statistics.put("shippingOrders", shippingOrders);
            statistics.put("completedOrders", completedOrders);
            statistics.put("aftersaleOrders", aftersaleOrders);
            
            // 今日订单数（这里简化处理，实际应该按日期查询）
            statistics.put("todayOrders", totalOrders);
            
            log.info("获取订单统计信息成功");
            return statistics;
            
        } catch (Exception e) {
            log.error("获取订单统计信息失败", e);
            throw new RuntimeException("获取订单统计信息失败", e);
        }
    }
    
    // ========== 支付相关接口实现 ==========
    
    @Override
    @Transactional
    public Order createOrderFromRequest(CreateOrderRequest request, String userId) {
        try {
            // 生成订单ID和订单号
            String orderId = UUID.randomUUID().toString();
            String orderNo = "ORDER" + System.currentTimeMillis() + (int)(Math.random() * 1000);
            
            // 创建订单实体
            Order order = new Order();
            order.setOrderId(orderId);
            order.setOrderNo(orderNo);
            order.setUserId(userId); // 设置用户ID
            order.setStatus("pending");
            order.setTotalPrice(request.getTotalAmount().doubleValue());
            order.setPaymentMethod(request.getPaymentMethod());
            order.setSource(request.getSource());
            order.setCreateTime(new Date());
            order.setExpireTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000)); // 30分钟过期
            
            // 处理优惠券
            BigDecimal finalAmount = request.getTotalAmount();
            if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
                // 提取项目ID列表
                List<String> projectIds = new ArrayList<>();
                if (request.getItems() != null) {
                    for (CreateOrderRequest.OrderItemDto item : request.getItems()) {
                        projectIds.add(item.getProjectId());
                    }
                }
                
                // 验证优惠券
                CouponValidationResult validationResult = couponValidationService.validateCoupon(
                    request.getCouponCode(),
                    userId,
                    request.getTotalAmount(),
                    projectIds
                );
                
                if (validationResult.isValid()) {
                    // 设置优惠券信息
                    order.setCouponCode(request.getCouponCode());
                    order.setCouponId(validationResult.getCoupon().getId());
                    order.setDiscountAmount(validationResult.getDiscountAmount().doubleValue());
                    
                    // 计算最终金额
                    finalAmount = request.getTotalAmount().subtract(validationResult.getDiscountAmount());
                    order.setFinalAmount(finalAmount.doubleValue());
                    
                    // 记录优惠券使用
                    recordCouponUsage(orderId, validationResult.getCoupon(), request.getTotalAmount(), finalAmount, userId);
                } else {
                    throw new RuntimeException("优惠券验证失败：" + validationResult.getMessage());
                }
            } else {
                // 没有使用优惠券
                order.setFinalAmount(request.getTotalAmount().doubleValue());
            }
            
            // 先保存订单（不包含items）
            order.setItems(new ArrayList<>());
            Order savedOrder = orderRepository.save(order);
            
            // 创建订单项目
            if (request.getItems() != null && !request.getItems().isEmpty()) {
                List<OrderItem> orderItems = new ArrayList<>();
                for (CreateOrderRequest.OrderItemDto itemDto : request.getItems()) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(orderId); // 设置订单ID
                    orderItem.setProjectId(itemDto.getProjectId());
                    orderItem.setProjectName(itemDto.getProjectName());
                    orderItem.setPrice(itemDto.getPrice());
                    orderItem.setQuantity(itemDto.getQuantity());
                    orderItem.setDuration(itemDto.getDuration());
                    orderItem.setTechnicianId(itemDto.getTechnicianId());
                    orderItem.setTimeSlot(itemDto.getTimeSlot());
                    orderItems.add(orderItem);
                }
                // 使用OrderItemRepository保存订单项
                orderItemRepository.saveAll(orderItems);
                // 设置订单项目
                savedOrder.setItems(orderItems);
            }
            
            log.info("创建订单成功，订单号：{}，原金额：{}，优惠金额：{}，最终金额：{}", 
                orderNo, request.getTotalAmount(), order.getDiscountAmount(), order.getFinalAmount());
            return savedOrder;
            
        } catch (Exception e) {
            log.error("创建订单失败", e);
            throw new RuntimeException("创建订单失败", e);
        }
    }
    
    @Override
    public Order getOrderByOrderNo(String orderNo) {
        try {
            return orderRepository.findByOrderNo(orderNo)
                    .orElseThrow(() -> new RuntimeException("订单不存在，订单号：" + orderNo));
        } catch (Exception e) {
            log.error("根据订单号查询订单失败，订单号：{}", orderNo, e);
            throw new RuntimeException("查询订单失败", e);
        }
    }
    
    @Override
    public WechatPaymentParams getWechatPaymentParams(UUID orderId) {
        try {
            Order order = getOrderDetail(orderId);
            String userId = order.getUserId();
            if (userId == null || userId.isEmpty()) {
                throw new RuntimeException("订单缺少用户信息");
            }
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            String openId = user.getOpenId();
            if (openId == null || openId.isEmpty()) {
                throw new RuntimeException("用户未绑定微信，无法发起支付");
            }
            return wechatPayService.createJsapiPaymentParams(order, openId);
        } catch (Exception e) {
            log.error("获取微信支付参数失败，订单ID：{}", orderId, e);
            throw new RuntimeException("获取微信支付参数失败：" + e.getMessage());
        }
    }
    
    @Override
    public String queryPaymentStatus(UUID orderId) {
        try {
            Order order = getOrderDetail(orderId);
            return order.getStatus();
        } catch (Exception e) {
            log.error("查询支付状态失败，订单ID：{}", orderId, e);
            throw new RuntimeException("查询支付状态失败", e);
        }
    }
    
    @Override
    @Transactional
    public void updateOrderStatus(String orderNo, String status, String transactionId) {
        try {
            Order order = getOrderByOrderNo(orderNo);
            order.setStatus(status);
            if (transactionId != null) {
                order.setWechatTransactionId(transactionId);
            }
            if ("paid".equals(status)) {
                order.setPayTime(new Date());
            }
            
            orderRepository.save(order);
            log.info("更新订单状态成功，订单号：{}，状态：{}", orderNo, status);
            
        } catch (Exception e) {
            log.error("更新订单状态失败，订单号：{}，状态：{}", orderNo, status, e);
            throw new RuntimeException("更新订单状态失败", e);
        }
    }
    
    @Override
    public boolean handlePaymentCallback(String callbackData) {
        try {
            log.info("收到支付回调，长度：{}", callbackData != null ? callbackData.length() : 0);
            return wechatPayService.handleNotify(callbackData);
        } catch (Exception e) {
            log.error("处理支付回调失败", e);
            return false;
        }
    }
    
    // ========== 优惠券相关接口实现 ==========
    
    @Override
    public CouponValidationResult validateCoupon(String couponCode, String userId, BigDecimal orderAmount, List<String> projectIds) {
        try {
            return couponValidationService.validateCoupon(couponCode, userId, orderAmount, projectIds);
        } catch (Exception e) {
            log.error("验证优惠券失败，优惠券代码：{}，用户ID：{}", couponCode, userId, e);
            throw new RuntimeException("验证优惠券失败", e);
        }
    }
    
    @Override
    public List<Coupon> getAvailableCoupons(String userId, BigDecimal orderAmount, List<String> projectIds) {
        try {
            return couponValidationService.getAvailableCoupons(userId, orderAmount, projectIds);
        } catch (Exception e) {
            log.error("获取可用优惠券失败，用户ID：{}，订单金额：{}", userId, orderAmount, e);
            throw new RuntimeException("获取可用优惠券失败", e);
        }
    }
    
    @Override
    public BigDecimal calculateFinalAmount(BigDecimal originalAmount, String couponCode, String userId, List<String> projectIds) {
        try {
            if (couponCode == null || couponCode.trim().isEmpty()) {
                return originalAmount;
            }
            
            CouponValidationResult result = validateCoupon(couponCode, userId, originalAmount, projectIds);
            if (result.isValid()) {
                return originalAmount.subtract(result.getDiscountAmount());
            } else {
                throw new RuntimeException("优惠券验证失败：" + result.getMessage());
            }
        } catch (Exception e) {
            log.error("计算最终金额失败，原金额：{}，优惠券代码：{}", originalAmount, couponCode, e);
            throw new RuntimeException("计算最终金额失败", e);
        }
    }
    
    /**
     * 记录优惠券使用
     */
    @Transactional
    private void recordCouponUsage(String orderId, Coupon coupon, BigDecimal originalAmount, BigDecimal finalAmount, String userId) {
        try {
            OrderCouponUsage usage = new OrderCouponUsage();
            usage.setOrderId(orderId);
            usage.setCouponId(coupon.getId());
            usage.setCouponCode(coupon.getCouponCode());
            usage.setUserId(userId);
            usage.setDiscountAmount(originalAmount.subtract(finalAmount));
            usage.setOriginalAmount(originalAmount);
            usage.setFinalAmount(finalAmount);
            usage.setUsedAt(LocalDateTime.now());
            usage.setStatus("used");
            
            orderCouponUsageRepository.save(usage);
            
            // 更新优惠券使用数量
            coupon.setUsedQuantity(coupon.getUsedQuantity() + 1);
            coupon.setRemainingQuantity(coupon.getTotalQuantity() - coupon.getUsedQuantity());
            
            log.info("记录优惠券使用成功，订单ID：{}，优惠券代码：{}", orderId, coupon.getCouponCode());
            
        } catch (Exception e) {
            log.error("记录优惠券使用失败，订单ID：{}，优惠券代码：{}", orderId, coupon.getCouponCode(), e);
            throw new RuntimeException("记录优惠券使用失败", e);
        }
    }

    @Override
    @Transactional
    public String generateVerificationCode(String orderId, String userId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));
        if (!userId.equals(order.getUserId())) {
            throw new RuntimeException("无权操作该订单");
        }
        if (!"paid".equals(order.getStatus())) {
            throw new RuntimeException("仅已支付订单可生成核销码");
        }
        String code = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        order.setVerificationCode(code);
        orderRepository.save(order);
        log.info("订单{}生成核销码", orderId);
        return code;
    }

    @Override
    public String getVerificationCode(String orderId, String userId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));
        if (!userId.equals(order.getUserId())) {
            throw new RuntimeException("无权查看该订单");
        }
        return order.getVerificationCode();
    }

    @Override
    @Transactional
    public Order consumeVerificationCode(String verificationCode, String operatorUserId) {
        if (verificationCode == null || verificationCode.isEmpty()) {
            throw new RuntimeException("核销码不能为空");
        }
        Order order = orderRepository.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new RuntimeException("核销码无效"));
        // 仅已支付或服务中的订单允许核销
        if (!"paid".equals(order.getStatus()) && !"shipping".equals(order.getStatus())) {
            throw new RuntimeException("当前订单状态不支持核销");
        }
        // 将订单状态置为已完成，并清除核销码防止重复使用
        order.setStatus("completed");
        order.setVerificationCode(null);
        Order updated = orderRepository.save(order);
        log.info("订单{}已由用户{}完成核销", order.getOrderId(), operatorUserId);
        return updated;
    }
}