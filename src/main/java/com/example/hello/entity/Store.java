package com.example.hello.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "stores")
public class Store {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String name;
    private String address;
    private String phone;
    private String businessHours;
    private Double latitude;
    private Double longitude;
}