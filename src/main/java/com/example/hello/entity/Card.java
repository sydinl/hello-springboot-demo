package com.example.hello.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "cards")
public class Card {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String userId;
    private String name;
    private String type;
    private Double balance;
    private Date expiryDate;
    private String status; // unused/used/expired
    private Date createTime;
    private Date useTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }
}