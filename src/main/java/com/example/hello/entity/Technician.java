package com.example.hello.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "technicians")
public class Technician {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String name;
    private String avatar;
    private String experience;
    private Double rating;
    
    @ElementCollection
    private List<String> services;
    
    private UUID storeId;
}