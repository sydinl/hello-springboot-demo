package com.example.hello.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "withdrawals")
public class Withdrawal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String userId;
    private Double amount;
    private String status;
    private Date createTime;
    private Date completeTime;
    private String accountInfo;
    
    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }
}