package com.example.hello.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "replies")
public class Reply {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String content;
    private Date createTime;
    private String reviewId;
    
    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }
}