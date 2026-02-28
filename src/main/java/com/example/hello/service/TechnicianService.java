package com.example.hello.service;

import com.example.hello.entity.Technician;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface TechnicianService {
    // 获取技师列表
    Page<Technician> getTechnicianList(Pageable pageable);
    
    // 搜索技师
    Page<Technician> searchTechnicians(String keyword, Pageable pageable);
    
    // 按门店获取技师列表
    Page<Technician> getTechniciansByStore(UUID storeId, Pageable pageable);
    
    // 获取技师详情
    Technician getTechnicianDetail(UUID technicianId);
}