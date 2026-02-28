package com.example.hello.controller;

import com.example.hello.entity.PointRecord;
import com.example.hello.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/point")
public class PointController {
    
    @Autowired
    private PointService pointService;
    
    // 获取用户积分余额
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Integer>> getUserPoints(@RequestParam UUID userId) {
        Integer points = pointService.getUserPoints(userId);
        return ResponseEntity.ok(Map.of("points", points));
    }
    
    // 获取用户积分记录列表
    @GetMapping("/records")
    public ResponseEntity<Page<PointRecord>> getUserPointRecords(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PointRecord> records = pointService.getUserPointRecords(userId, pageable);
        return ResponseEntity.ok(records);
    }
    
    // 获取用户指定类型的积分记录
    @GetMapping("/recordsByType")
    public ResponseEntity<Page<PointRecord>> getUserPointRecordsByType(
            @RequestParam UUID userId,
            @RequestParam String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PointRecord> records = pointService.getUserPointRecordsByType(userId, type, pageable);
        return ResponseEntity.ok(records);
    }
}