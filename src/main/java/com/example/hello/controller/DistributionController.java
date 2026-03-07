package com.example.hello.controller;

import com.example.hello.common.ApiResponse;
import com.example.hello.entity.DistributionData;
import com.example.hello.entity.DistributionOrder;
import com.example.hello.entity.User;
import com.example.hello.entity.Withdrawal;
import com.example.hello.repository.UserRepository;
import com.example.hello.service.DistributionService;
import com.example.hello.service.WithdrawalService;
import com.example.hello.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/api/distribution")
public class DistributionController {
    
    @Autowired
    private DistributionService distributionService;

    @Autowired
    private WithdrawalService withdrawalService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenUtil tokenUtil;
    
    /** 获取分销中心数据（前端推荐：带 token 即可，无需传 userId） */
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<DistributionData>> getDistributionDataByToken(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = tokenUtil.extractTokenFromHeader(authorization);
        String userId = token != null ? tokenUtil.getUserIdFromToken(token) : null;
        if (!StringUtils.hasText(userId)) {
            return ResponseEntity.ok(ApiResponse.error(401, "请先登录"));
        }
        try {
            DistributionData data = distributionService.getDistributionData(UUID.fromString(userId));
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.error(400, "用户ID无效"));
        }
    }

    /** 获取分销中心数据（兼容旧版：传 userId 参数） */
    @GetMapping("/centerData")
    public ResponseEntity<DistributionData> getDistributionData(@RequestParam UUID userId) {
        DistributionData data = distributionService.getDistributionData(userId);
        return ResponseEntity.ok(data);
    }
    
    /** 获取分销订单列表（推荐：带 token，分页 + 可选状态） */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDistributionOrdersByToken(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        String token = tokenUtil.extractTokenFromHeader(authorization);
        String userId = token != null ? tokenUtil.getUserIdFromToken(token) : null;
        if (!StringUtils.hasText(userId)) {
            return ResponseEntity.ok(ApiResponse.error(401, "请先登录"));
        }
        try {
            int page0 = page <= 0 ? 0 : page - 1;
            Pageable pageable = PageRequest.of(page0, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
            Page<DistributionOrder> result;
            if (StringUtils.hasText(status) && !"all".equalsIgnoreCase(status)) {
                result = distributionService.getDistributionOrdersByStatus(java.util.UUID.fromString(userId), status, pageable);
            } else {
                result = distributionService.getDistributionOrders(java.util.UUID.fromString(userId), pageable);
            }
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("content", result.getContent());
            data.put("totalElements", result.getTotalElements());
            data.put("totalPages", result.getTotalPages());
            data.put("number", result.getNumber());
            data.put("size", result.getSize());
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.error(400, "用户ID无效"));
        }
    }

    /** 获取分销订单列表（兼容：传 userId 参数） */
    @GetMapping("/ordersByStatus")
    public ResponseEntity<Page<DistributionOrder>> getDistributionOrdersByStatus(
            @RequestParam UUID userId,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
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

    /** 提现记录列表（带 token，分页，可选 status） */
    @GetMapping("/withdrawals")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWithdrawals(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        String token = tokenUtil.extractTokenFromHeader(authorization);
        String userId = token != null ? tokenUtil.getUserIdFromToken(token) : null;
        if (!StringUtils.hasText(userId)) {
            return ResponseEntity.ok(ApiResponse.error(401, "请先登录"));
        }
        try {
            int page0 = page <= 0 ? 0 : page - 1;
            Pageable pageable = PageRequest.of(page0, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
            Page<Withdrawal> result = StringUtils.hasText(status) && !"all".equalsIgnoreCase(status)
                    ? withdrawalService.getWithdrawalRecordsByStatus(UUID.fromString(userId), status, pageable)
                    : withdrawalService.getWithdrawalRecords(UUID.fromString(userId), pageable);
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("content", result.getContent());
            data.put("totalElements", result.getTotalElements());
            data.put("totalPages", result.getTotalPages());
            data.put("number", result.getNumber());
            data.put("size", result.getSize());
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.error(400, "用户ID无效"));
        }
    }

    /** 申请提现。body: { "amount": 100.5 } */
    @PostMapping("/applyWithdrawal")
    public ResponseEntity<ApiResponse<Withdrawal>> applyWithdrawal(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, Object> body) {
        String token = tokenUtil.extractTokenFromHeader(authorization);
        String userId = token != null ? tokenUtil.getUserIdFromToken(token) : null;
        if (!StringUtils.hasText(userId)) {
            return ResponseEntity.ok(ApiResponse.error(401, "请先登录"));
        }
        Object a = body != null ? body.get("amount") : null;
        double amount = a instanceof Number ? ((Number) a).doubleValue() : (a != null ? Double.parseDouble(a.toString()) : 0);
        if (amount <= 0) {
            return ResponseEntity.ok(ApiResponse.error(400, "提现金额必须大于0"));
        }
        try {
            Withdrawal w = withdrawalService.applyWithdrawal(UUID.fromString(userId), amount);
            return ResponseEntity.ok(ApiResponse.success(w));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.error(400, e.getMessage()));
        }
    }

    /** 我的团队：一级、二级下级列表（脱敏） */
    @GetMapping("/team")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyTeam(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = tokenUtil.extractTokenFromHeader(authorization);
        String userId = token != null ? tokenUtil.getUserIdFromToken(token) : null;
        if (!StringUtils.hasText(userId)) {
            return ResponseEntity.ok(ApiResponse.error(401, "请先登录"));
        }
        List<User> level1List = userRepository.findByReferrerId(userId);
        List<Map<String, Object>> level1 = new java.util.ArrayList<>();
        List<Map<String, Object>> level2 = new java.util.ArrayList<>();
        for (User u : level1List) {
            level1.add(userToTeamMember(u));
            for (User u2 : userRepository.findByReferrerId(u.getId())) {
                level2.add(userToTeamMember(u2));
            }
        }
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("level1", level1);
        data.put("level2", level2);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    private static Map<String, Object> userToTeamMember(User u) {
        Map<String, Object> m = new java.util.HashMap<>();
        m.put("id", u.getId());
        m.put("username", u.getUsername());
        m.put("avatar", u.getAvatar());
        m.put("fullName", u.getFullName());
        m.put("phone", u.getPhone() != null ? u.getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2") : null);
        return m;
    }

    /** 推广信息：当前用户 ID 作为推荐人，用于生成推广链接/小程序码 scene */
    @GetMapping("/promotion-info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPromotionInfo(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = tokenUtil.extractTokenFromHeader(authorization);
        String userId = token != null ? tokenUtil.getUserIdFromToken(token) : null;
        if (!StringUtils.hasText(userId)) {
            return ResponseEntity.ok(ApiResponse.error(401, "请先登录"));
        }
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("referrerId", userId);
        data.put("scene", "referrerId=" + userId);
        data.put("invitePath", "/pages/index/index?referrerId=" + userId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}