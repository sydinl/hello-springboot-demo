package com.example.hello.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "project_images")
public class ProjectImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private UUID projectId;
    private String url;
    private String description;
}