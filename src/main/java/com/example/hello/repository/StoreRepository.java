package com.example.hello.repository;

import com.example.hello.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {
    // 按名称模糊查询门店
    Page<Store> findByNameContaining(String keyword, Pageable pageable);
    
    // 按地区查询门店
    Page<Store> findByAddressContaining(String area, Pageable pageable);
}