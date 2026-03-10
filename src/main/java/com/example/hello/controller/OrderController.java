package com.example.hello.controller;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.hello.common.ApiResponse;
import com.example.hello.common.RateLimit;
import com.example.hello.dto.CreateOrderRequest;
import com.example.hello.dto.WechatPaymentParams;
import com.example.hello.entity.Coupon;
import com.example.hello.entity.Order;
import com.example.hello.service.CouponValidationService.CouponValidationResult;
import com.example.hello.service.OrderService;
import com.example.hello.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

@RestController
@RequestMapping("/api/order")
@RateLimit(maxRequests = 100, timeWindow = 60, message = "订单API调用频率过高，请稍后再试")
@Slf4j
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private TokenUtil tokenUtil;
    
    // 创建订单 - 严格限流
    @PostMapping
    @RateLimit(maxRequests = 20, timeWindow = 60, perUser = true, message = "订单创建过于频繁，请稍后再试")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }
    
    // 通过CreateOrderRequest创建订单（支付相关）
    @PostMapping("/create")
    @RateLimit(maxRequests = 20, timeWindow = 60, perUser = true, message = "订单创建过于频繁，请稍后再试")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrderFromRequest(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreateOrderRequest request) {
        try {
            // 获取用户ID
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "认证失败，请重新登录"));
            }
            
            // 参数校验
            if (request.getItems() == null || request.getItems().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(1001, "订单项目不能为空"));
            }
            
            if (request.getTotalAmount() == null || request.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return ResponseEntity.ok(ApiResponse.error(1001, "订单金额必须大于0"));
            }
            
            if (request.getPaymentMethod() == null || request.getPaymentMethod().trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(1001, "支付方式不能为空"));
            }
            
            if (!"balance".equals(request.getPaymentMethod()) && !"wechat".equals(request.getPaymentMethod())) {
                return ResponseEntity.ok(ApiResponse.error(1001, "支付方式只能是balance或wechat"));
            }
            
            // 创建订单
            Order order = orderService.createOrderFromRequest(request, userId);
            
            // 构建响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("orderId", order.getOrderId());
            data.put("orderNo", order.getOrderNo());
            data.put("status", order.getStatus());
            data.put("totalAmount", order.getTotalPrice());
            data.put("paymentMethod", order.getPaymentMethod());
            data.put("createTime", order.getCreateTime());
            data.put("expireTime", order.getExpireTime());
            
            return ResponseEntity.ok(ApiResponse.success(data));
            
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return ResponseEntity.ok(ApiResponse.error(3001, "系统错误"));
        }
    }
    
    // 获取订单详情（带订单项，需登录且仅能查本人订单）
    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<Order>> getOrderDetail(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam String orderId) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "请先登录"));
            }
            Order order = orderService.getOrderDetail(UUID.fromString(orderId));
            if (!userId.equals(order.getUserId())) {
                return ResponseEntity.ok(ApiResponse.error(2003, "无权查看该订单"));
            }
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.error(1001, "订单ID格式错误"));
        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            return ResponseEntity.ok(ApiResponse.error(3001, e.getMessage()));
        }
    }

    // 生成核销码（仅已支付订单，需登录）
    @PostMapping("/verification/generate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateVerificationCode(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> body) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "请先登录"));
            }
            String orderId = body != null ? body.get("orderId") : null;
            if (orderId == null || orderId.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(1001, "订单ID不能为空"));
            }
            String code = orderService.generateVerificationCode(orderId, userId);
            Map<String, Object> data = new HashMap<>();
            data.put("verificationCode", code);
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            log.error("生成核销码失败", e);
            return ResponseEntity.ok(ApiResponse.error(3001, e.getMessage()));
        }
    }

    // 获取核销码（需登录）
    @GetMapping("/verification/get")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVerificationCode(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String orderId) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "请先登录"));
            }
            String code = orderService.getVerificationCode(orderId, userId);
            Map<String, Object> data = new HashMap<>();
            data.put("verificationCode", code);
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            log.error("获取核销码失败", e);
            return ResponseEntity.ok(ApiResponse.error(3001, e.getMessage()));
        }
    }

    // 获取核销码二维码（需登录）
    @GetMapping("/verification/qrcode")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVerificationQrcode(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String orderId) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "请先登录"));
            }
            if (orderId == null || orderId.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(1001, "订单ID不能为空"));
            }
            // 优先获取现有核销码，没有则尝试生成
            String code;
            try {
                code = orderService.getVerificationCode(orderId, userId);
            } catch (Exception e) {
                // 若尚未生成且提示“仅已支付订单可生成核销码”，则尝试自动生成
                code = orderService.generateVerificationCode(orderId, userId);
            }
            if (code == null || code.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(3001, "核销码不存在或生成失败"));
            }

            // 生成二维码内容：简单使用核销码本身
            String qrContent = code;
            int size = 280;
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            java.util.Map<EncodeHintType, Object> hints = new java.util.HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, size, size, hints);
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            Map<String, Object> data = new HashMap<>();
            data.put("imageBase64", base64);
            data.put("mimeType", "image/png");
            data.put("verificationCode", code);
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            log.error("生成核销码二维码失败", e);
            return ResponseEntity.ok(ApiResponse.error(3001, "生成二维码失败：" + e.getMessage()));
        }
    }
    
    // 按用户ID查询订单列表
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrderListByUserId(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "认证失败，请重新登录"));
            }
            
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Order> orders = orderService.getOrderListByUserId(userId, pageable);
            log.info("User ID: {}, Orders count: {}", userId, orders.getTotalElements());
            // 构建分页响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", orders.getContent());
            responseData.put("totalElements", orders.getTotalElements());
            responseData.put("totalPages", orders.getTotalPages());
            responseData.put("size", orders.getSize());
            responseData.put("number", orders.getNumber());
            responseData.put("first", orders.isFirst());
            responseData.put("last", orders.isLast());
            responseData.put("numberOfElements", orders.getNumberOfElements());
            
            return ResponseEntity.ok(ApiResponse.success(responseData));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(3001, "获取订单列表失败：" + e.getMessage()));
        }
    }
    
    // 按用户ID和状态查询订单列表
    @GetMapping("/listByStatus")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrderListByUserIdAndStatus(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "认证失败，请重新登录"));
            }
            log.info("用户ID,status：{},{}", userId, status);
            // 添加排序确保分页的稳定性，页码从0开始，所以需要减1
            Pageable pageable = PageRequest.of(page - 1, size, org.springframework.data.domain.Sort.by("createTime").descending());
            
            Page<Order> orders;
            // 如果status为all，返回所有订单；否则按状态筛选
            if ("all".equalsIgnoreCase(status)) {
                log.info("查询用户所有订单，用户ID：{}", userId);
                orders = orderService.getOrderListByUserId(userId, pageable);
            } else {
                log.info("按状态查询用户订单，用户ID：{}，状态：{}", userId, status);
                orders = orderService.getOrderListByUserIdAndStatus(userId, status, pageable);
            }
            log.info("User ID: {}, Orders count: {}", userId, orders.getTotalElements());
            // 构建分页响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", orders.getContent());
            responseData.put("totalElements", orders.getTotalElements());
            responseData.put("totalPages", orders.getTotalPages());
            responseData.put("size", orders.getSize());
            responseData.put("number", orders.getNumber());
            responseData.put("first", orders.isFirst());
            responseData.put("last", orders.isLast());
            responseData.put("numberOfElements", orders.getNumberOfElements());
            
            return ResponseEntity.ok(ApiResponse.success(responseData));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(3001, "获取订单列表失败：" + e.getMessage()));
        }
    }
    
    // 更新订单状态
    @PutMapping("/status")
    public ResponseEntity<Order> updateOrderStatus(@RequestParam UUID orderId, @RequestParam String status) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }
    
    // ========== 管理员订单管理接口 ==========
    
    // 获取所有订单（管理员用）
    @GetMapping("/admin/all")
    public ApiResponse<Map<String, Object>> getAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Order> orders = orderService.getAllOrders(status, userId, search, pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", orders.getTotalElements());
        result.put("list", orders.getContent());
        
        return ApiResponse.success(result);
    }
    
    // 获取订单详情（管理员用）
    @GetMapping("/admin/detail")
    public ApiResponse<Order> getOrderDetailForAdmin(@RequestParam String orderId) {
        try {
            Order order = orderService.getOrderDetail(UUID.fromString(orderId));
            return ApiResponse.success(order);
        } catch (Exception e) {
            return ApiResponse.error("获取订单详情失败：" + e.getMessage());
        }
    }
    
    // 更新订单状态（管理员用）
    @PutMapping("/admin/status/{orderId}")
    public ApiResponse<Order> updateOrderStatusForAdmin(@PathVariable String orderId, @RequestParam String status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(UUID.fromString(orderId), status);
            return ApiResponse.success(updatedOrder);
        } catch (Exception e) {
            return ApiResponse.error("更新订单状态失败：" + e.getMessage());
        }
    }
    
    // 获取订单统计信息
    @GetMapping("/admin/statistics")
    public ApiResponse<Map<String, Object>> getOrderStatistics() {
        try {
            Map<String, Object> statistics = orderService.getOrderStatistics();
            return ApiResponse.success(statistics);
        } catch (Exception e) {
            return ApiResponse.error("获取订单统计失败：" + e.getMessage());
        }
    }
    
    // ========== 支付相关接口 ==========
    
    // 获取微信支付参数
    @PostMapping("/payment/wechat/params")
    @RateLimit(maxRequests = 10, timeWindow = 60, perUser = true, message = "支付参数获取过于频繁，请稍后再试")
    public ResponseEntity<ApiResponse<WechatPaymentParams>> getWechatPaymentParams(@RequestBody Map<String, String> request) {
        try {
            String orderIdStr = request.get("orderId");
            if (orderIdStr == null || orderIdStr.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(1001, "订单ID不能为空"));
            }
            
            UUID orderId = UUID.fromString(orderIdStr);
            WechatPaymentParams params = orderService.getWechatPaymentParams(orderId);
            
            return ResponseEntity.ok(ApiResponse.success(params));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.error(1001, "订单ID格式错误"));
        } catch (Exception e) {
            log.error("获取微信支付参数失败", e);
            return ResponseEntity.ok(ApiResponse.error(1004, "支付参数生成失败"));
        }
    }
    
    // 查询支付状态
    @GetMapping("/payment/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> queryPaymentStatus(@RequestParam UUID orderId) {
        try {
            String status = orderService.queryPaymentStatus(orderId);
            Order order = orderService.getOrderDetail(orderId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("orderId", orderId);
            data.put("status", status);
            data.put("payTime", order.getPayTime());
            data.put("transactionId", order.getWechatTransactionId());
            
            return ResponseEntity.ok(ApiResponse.success(data));
            
        } catch (Exception e) {
            log.error("查询支付状态失败", e);
            return ResponseEntity.ok(ApiResponse.error(1005, "支付状态查询失败"));
        }
    }
    
    // 支付回调处理（微信 V2 要求返回 XML，否则会重复通知）
    @PostMapping("/payment/callback")
    @RateLimit(maxRequests = 200, timeWindow = 60, message = "支付回调处理过于频繁")
    public ResponseEntity<String> handlePaymentCallback(@RequestBody String callbackData) {
        try {
            log.info("收到支付回调，长度：{}", callbackData != null ? callbackData.length() : 0);
            boolean success = orderService.handlePaymentCallback(callbackData);
            String xml = success
                    ? "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>"
                    : "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml);
        } catch (Exception e) {
            log.error("处理支付回调失败", e);
            String xml = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[系统异常]]></return_msg></xml>";
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml);
        }
    }
    
    // ========== 优惠券相关接口 ==========
    
    // 验证优惠券
    @PostMapping("/coupon/validate")
    @RateLimit(maxRequests = 20, timeWindow = 60, perUser = true, message = "优惠券验证过于频繁，请稍后再试")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateCoupon(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, Object> request) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "认证失败，请重新登录"));
            }
            
            String couponCode = (String) request.get("couponCode");
            BigDecimal orderAmount = new BigDecimal(request.get("orderAmount").toString());
            @SuppressWarnings("unchecked")
            List<String> projectIds = (List<String>) request.get("projectIds");
            
            if (couponCode == null || couponCode.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error(1001, "优惠券代码不能为空"));
            }
            
            if (orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.ok(ApiResponse.error(1001, "订单金额必须大于0"));
            }
            
            CouponValidationResult result = orderService.validateCoupon(couponCode, userId, orderAmount, projectIds);
            
            Map<String, Object> data = new HashMap<>();
            data.put("valid", result.isValid());
            data.put("message", result.getMessage());
            if (result.isValid()) {
                data.put("couponId", result.getCoupon().getId());
                data.put("couponName", result.getCoupon().getCouponName());
                data.put("discountAmount", result.getDiscountAmount());
                data.put("finalAmount", orderAmount.subtract(result.getDiscountAmount()));
            }
            
            return ResponseEntity.ok(ApiResponse.success(data));
            
        } catch (Exception e) {
            log.error("验证优惠券失败", e);
            return ResponseEntity.ok(ApiResponse.error(3001, "系统错误"));
        }
    }
    
    // 获取可用优惠券列表
    @GetMapping("/coupon/available")
    public ResponseEntity<ApiResponse<List<Coupon>>> getAvailableCoupons(
            @RequestHeader("Authorization") String authorization,
            @RequestParam BigDecimal orderAmount,
            @RequestParam(required = false) List<String> projectIds) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "认证失败，请重新登录"));
            }
            
            if (orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.ok(ApiResponse.error(1001, "订单金额必须大于0"));
            }
            
            List<Coupon> coupons = orderService.getAvailableCoupons(userId, orderAmount, projectIds);
            return ResponseEntity.ok(ApiResponse.success(coupons));
            
        } catch (Exception e) {
            log.error("获取可用优惠券失败", e);
            return ResponseEntity.ok(ApiResponse.error(3001, "系统错误"));
        }
    }
    
    // 计算最终金额
    @PostMapping("/coupon/calculate")
    @RateLimit(maxRequests = 20, timeWindow = 60, perUser = true, message = "金额计算过于频繁，请稍后再试")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateFinalAmount(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, Object> request) {
        try {
            String userId = tokenUtil.getUserIdFromHeader(authorization);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error(2001, "认证失败，请重新登录"));
            }
            
            BigDecimal originalAmount = new BigDecimal(request.get("originalAmount").toString());
            String couponCode = (String) request.get("couponCode");
            @SuppressWarnings("unchecked")
            List<String> projectIds = (List<String>) request.get("projectIds");
            
            if (originalAmount == null || originalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.ok(ApiResponse.error(1001, "原金额必须大于0"));
            }
            
            BigDecimal finalAmount = orderService.calculateFinalAmount(originalAmount, couponCode, userId, projectIds);
            
            Map<String, Object> data = new HashMap<>();
            data.put("originalAmount", originalAmount);
            data.put("finalAmount", finalAmount);
            data.put("discountAmount", originalAmount.subtract(finalAmount));
            
            return ResponseEntity.ok(ApiResponse.success(data));
            
        } catch (Exception e) {
            log.error("计算最终金额失败", e);
            return ResponseEntity.ok(ApiResponse.error(3001, "系统错误"));
        }
    }
}