package com.example.hello.controller;

import com.example.hello.common.ApiResponse;
import com.example.hello.entity.DistributionData;
import com.example.hello.entity.DistributionOrder;
import com.example.hello.service.DistributionService;
import com.example.hello.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/distribution")
public class DistributionController {
    
    @Autowired
    private DistributionService distributionService;

    @Autowired
    private TokenUtil tokenUtil;
    
    // 获取分销中心数据
    @GetMapping("/centerData")
    public ResponseEntity<DistributionData> getDistributionData(@RequestParam UUID userId) {
        DistributionData data = distributionService.getDistributionData(userId);
        return ResponseEntity.ok(data);
    }
    
    // 获取分销订单列表
    @GetMapping("/orders")
    public ResponseEntity<Page<DistributionOrder>> getDistributionOrders(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DistributionOrder> orders = distributionService.getDistributionOrders(userId, pageable);
        return ResponseEntity.ok(orders);
    }
    
    // 获取指定状态的分销订单
    @GetMapping("/ordersByStatus")
    public ResponseEntity<Page<DistributionOrder>> getDistributionOrdersByStatus(
            @RequestParam UUID userId,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DistributionOrder> orders = distributionService.getDistributionOrdersByStatus(userId, status, pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * 绑定推荐人（二级分销一级关系）。仅当前用户未绑定过时有效。
     * 请求头需带 Authorization: Bearer &lt;token&gt;，body: { "referrerId": "推荐人用户ID" }
     */
    @PostMapping("/bind-referrer")
    public ResponseEntity<ApiResponse<Void>> bindReferrer(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, String> body) {
        String token = tokenUtil.extractTokenFromHeader(authorization);
        String userId = token != null ? tokenUtil.getUserIdFromToken(token) : null;
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "请先登录"));
        }
        String referrerId = body != null ? body.get("referrerId") : null;
        if (referrerId == null || referrerId.isBlank()) {
            return ResponseEntity.ok(ApiResponse.error(400, "缺少推荐人ID"));
        }
        boolean ok = distributionService.bindReferrer(userId, referrerId.trim());
        if (ok) {
            return ResponseEntity.ok(ApiResponse.success());
        }
        return ResponseEntity.ok(ApiResponse.error(400, "绑定失败：已绑定过推荐人、推荐人不存在或不能绑定自己"));
    }
}