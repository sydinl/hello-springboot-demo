package com.example.hello.service;

import com.example.hello.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface StoreService {
    // 获取门店列表
    Page<Store> getStoreList(Pageable pageable);
    
    // 搜索门店
    Page<Store> searchStores(String keyword, Pageable pageable);
    
    // 按地区获取门店列表
    Page<Store> getStoresByArea(String area, Pageable pageable);
    
    // 获取门店详情
    Store getStoreDetail(UUID storeId);
}