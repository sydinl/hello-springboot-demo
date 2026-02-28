package com.example.hello.controller;

import com.example.hello.entity.Store;
import com.example.hello.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/store")
public class StoreController {
    
    @Autowired
    private StoreService storeService;
    
    // 获取门店列表
    @GetMapping("/list")
    public ResponseEntity<Page<Store>> getStoreList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Store> stores = storeService.getStoreList(pageable);
        return ResponseEntity.ok(stores);
    }
    
    // 搜索门店
    @GetMapping("/search")
    public ResponseEntity<Page<Store>> searchStores(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Store> stores = storeService.searchStores(keyword, pageable);
        return ResponseEntity.ok(stores);
    }
    
    // 按地区获取门店列表
    @GetMapping("/byArea")
    public ResponseEntity<Page<Store>> getStoresByArea(
            @RequestParam String area,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Store> stores = storeService.getStoresByArea(area, pageable);
        return ResponseEntity.ok(stores);
    }
    
    // 获取门店详情
    @GetMapping("/detail")
    public ResponseEntity<Store> getStoreDetail(@RequestParam UUID storeId) {
        Store store = storeService.getStoreDetail(storeId);
        return ResponseEntity.ok(store);
    }
}