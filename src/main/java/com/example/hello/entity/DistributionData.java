package com.example.hello.entity;

import lombok.Data;

@Data
public class DistributionData {
    
    private Double totalCommission;
    private Double availableCommission;
    private Integer teamCount;
    private Integer todayOrderCount;
}