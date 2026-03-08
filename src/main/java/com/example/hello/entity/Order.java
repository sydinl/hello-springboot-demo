package com.example.hello.entity;

import java.util.Date;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private String orderId;
    
    @Column(name = "user_id")
    private String userId;
    
    private String status; // pending/paid/shipping/completed/aftersale
    
    @Column(name = "total_price")
    private Double totalPrice;
    
    @Column(name = "create_time")
    private Date createTime;
    
    @Column(name = "pay_time")
    private Date payTime;
    
    @Column(name = "service_time")
    private Date serviceTime;
    
    @Column(name = "address_id")
    private String addressId;
    
    @Column(name = "technician_id")
    private String technicianId;
    
    @Column(name = "coupon_id")
    private String couponId;
    
    @Column(name = "coupon_code")
    private String couponCode; // 优惠券代码
    
    @Column(name = "discount_amount")
    private Double discountAmount; // 优惠金额
    
    @Column(name = "final_amount")
    private Double finalAmount; // 最终支付金额
    
    @Column(name = "payment_method")
    private String paymentMethod; // balance, wechat
    
    @Column(name = "order_no")
    private String orderNo; // 订单号
    
    private String source; // 订单来源：cart, detail, order
    
    @Column(name = "wechat_transaction_id")
    private String wechatTransactionId; // 微信支付交易号
    
    @Column(name = "wechat_prepay_id")
    private String wechatPrepayId; // 微信预支付ID
    
    @Column(name = "expire_time")
    private Date expireTime; // 订单过期时间
    
    private String remarks;
    
    @Column(name = "verification_code", length = 32)
    private String verificationCode; // 核销码（已支付订单可生成）
    
    @OneToMany(mappedBy = "orderId", cascade = CascadeType.ALL, fetch = jakarta.persistence.FetchType.LAZY)
    private List<OrderItem> items;
    
    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }
}