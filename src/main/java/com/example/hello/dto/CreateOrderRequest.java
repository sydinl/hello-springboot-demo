package com.example.hello.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建订单请求DTO
 */
@Data
public class CreateOrderRequest {
    
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String source;
    private LocalDateTime createTime;
    private String orderNo;
    private String status;
    
    // 优惠券相关字段
    private String couponCode; // 优惠券代码
    private BigDecimal discountAmount; // 优惠金额
    private BigDecimal finalAmount; // 最终支付金额
    
    @Data
    public static class OrderItemDto {
        private String projectId;
        private String projectName;
        private BigDecimal price;
        private Integer quantity;
        private String duration;
        private String technicianId;
        private String timeSlot;
    }
}



