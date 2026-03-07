package com.example.hello.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "distribution_orders")
public class DistributionOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String orderId;
    /** 获得佣金的推荐人用户ID */
    private String referrerId;
    /** 二级分销层级：1=一级推荐人，2=二级推荐人 */
    @Column(name = "referrer_level")
    private Integer referrerLevel;
    private String customerName;
    private Double commission;
    private String status; // pending/settled
    private Date createTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }
}