package com.example.hello.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "project_categories")
public class ProjectCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String name;
    private String icon;
    private Integer projectCount;
}