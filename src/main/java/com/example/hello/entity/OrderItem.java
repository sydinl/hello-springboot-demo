package com.example.hello.entity;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 订单项目实体
 */
@Data
@Entity
@Table(name = "order_items")
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_id")
    private String id;
    
    @Column(name = "order_id")
    private String orderId; // 所属订单ID
    
    @Column(name = "project_id")
    private String projectId;
    
    @Column(name = "name")
    private String projectName;
    
    private BigDecimal price;
    
    @Column(name = "count")
    private Integer quantity;
    
    private String duration;
    
    private String technicianId;
    private String timeSlot;
    
    public OrderItem() {}
    
    public OrderItem(String projectId, String projectName, BigDecimal price, Integer quantity, 
                    String duration, String technicianId, String timeSlot) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.price = price;
        this.quantity = quantity;
        this.duration = duration;
        this.technicianId = technicianId;
        this.timeSlot = timeSlot;
    }
}