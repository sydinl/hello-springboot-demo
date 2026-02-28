package com.example.hello.controller;

import com.example.hello.entity.Technician;
import com.example.hello.service.TechnicianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {
    
    @Autowired
    private TechnicianService technicianService;
    
    // 获取技师列表
    @GetMapping("/list")
    public ResponseEntity<Page<Technician>> getTechnicianList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Technician> technicians = technicianService.getTechnicianList(pageable);
        return ResponseEntity.ok(technicians);
    }
    
    // 搜索技师
    @GetMapping("/search")
    public ResponseEntity<Page<Technician>> searchTechnicians(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Technician> technicians = technicianService.searchTechnicians(keyword, pageable);
        return ResponseEntity.ok(technicians);
    }
    
    // 按门店获取技师列表
    @GetMapping("/byStore")
    public ResponseEntity<Page<Technician>> getTechniciansByStore(
            @RequestParam UUID storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Technician> technicians = technicianService.getTechniciansByStore(storeId, pageable);
        return ResponseEntity.ok(technicians);
    }
    
    // 获取技师详情
    @GetMapping("/detail")
    public ResponseEntity<Technician> getTechnicianDetail(@RequestParam UUID technicianId) {
        Technician technician = technicianService.getTechnicianDetail(technicianId);
        return ResponseEntity.ok(technician);
    }
}