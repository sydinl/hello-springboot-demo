package com.example.hello.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "reviews")
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String userId;
    private String projectId;
    private Integer rating;
    private String content;
    private Date createTime;
    private Integer technicianRating;
    private Integer environmentRating;
    private Integer serviceRating;
    
    @ElementCollection
    private List<String> images;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "review_id")
    private Reply reply;
    
    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }
}