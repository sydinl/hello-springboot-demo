package com.example.hello.entity;

import java.util.Date;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "projects")
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String name;
    private String description;
    private Double price;
    private String image;
    private String duration;
    private String category;
    private String categoryId; // 分类ID，关联到project_categories表
    private String details;
    private Integer salesCount;
    private Double rating;
    private Boolean isHot;
    private Boolean isRecommend;
    private String status; // active, inactive, draft
    
    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL)
    private List<ProjectImage> images;
    
    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL)
    private List<Review> reviews;
    
    private Date createTime;
    private Date updateTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
}