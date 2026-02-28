package com.example.hello.repository;

import com.example.hello.entity.Technician;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, UUID> {
    // 按名称模糊查询技师
    Page<Technician> findByNameContaining(String keyword, Pageable pageable);
    
    // 按门店ID分页查询技师
    Page<Technician> findByStoreId(UUID storeId, Pageable pageable);
}