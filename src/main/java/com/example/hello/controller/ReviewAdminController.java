package com.example.hello.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.hello.annotation.AuditLog;
import com.example.hello.common.ApiResponse;
import com.example.hello.entity.Review;
import com.example.hello.service.ReviewService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/reviews")
@Slf4j
public class ReviewAdminController {
    
    @Autowired
    private ReviewService reviewService;
    
    // 获取评价管理页面
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public String reviewManagementPage() {
        return "admin/review-management";
    }
    
    // 获取评价列表
    @GetMapping("/api/list")
    @PreAuthorize("hasRole('ADMIN')")
    @AuditLog(operation = "VIEW", resourceType = "REVIEW", description = "查看评价列表（管理）")
    public ApiResponse<Map<String, Object>> getReviewList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Review> reviews;
        
        if (search != null && !search.isEmpty()) {
            reviews = reviewService.searchReviews(search, pageable);
        } else if (status != null && !status.isEmpty()) {
            reviews = reviewService.getReviewsByStatus(status, pageable);
        } else {
            reviews = reviewService.getAllReviews(pageable);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", reviews.getTotalElements());
        result.put("list", reviews.getContent());
        
        return ApiResponse.success(result);
    }
    
    // 获取评价详情
    @GetMapping("/api/detail/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    @AuditLog(operation = "VIEW", resourceType = "REVIEW", description = "查看评价详情")
    public ApiResponse<Review> getReviewDetail(@PathVariable String reviewId) {
        try {
            Review review = reviewService.getReviewDetail(reviewId);
            return ApiResponse.success(review);
        } catch (Exception e) {
            return ApiResponse.error("获取评价详情失败：" + e.getMessage());
        }
    }
    
    // 删除评价
    @DeleteMapping("/api/delete/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    @AuditLog(operation = "DELETE", resourceType = "REVIEW", description = "删除评价")
    public ApiResponse<String> deleteReview(@PathVariable String reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return ApiResponse.success("删除评价成功");
        } catch (Exception e) {
            return ApiResponse.error("删除评价失败：" + e.getMessage());
        }
    }
    
    // 批量删除评价
    @DeleteMapping("/api/batch-delete")
    @PreAuthorize("hasRole('ADMIN')")
    @AuditLog(operation = "DELETE", resourceType = "REVIEW", description = "批量删除评价")
    public ApiResponse<String> deleteReviews(@RequestBody String[] reviewIds) {
        try {
            reviewService.deleteReviews(reviewIds);
            return ApiResponse.success("批量删除评价成功");
        } catch (Exception e) {
            return ApiResponse.error("批量删除评价失败：" + e.getMessage());
        }
    }
    
    // 更新评价状态
    @PutMapping("/api/update-status/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    @AuditLog(operation = "UPDATE", resourceType = "REVIEW", description = "更新评价状态")
    public ApiResponse<Review> updateReviewStatus(@PathVariable String reviewId, @RequestParam String status) {
        try {
            Review updatedReview = reviewService.updateReviewStatus(reviewId, status);
            return ApiResponse.success(updatedReview);
        } catch (Exception e) {
            return ApiResponse.error("更新评价状态失败：" + e.getMessage());
        }
    }
    
    // 获取评价统计信息
    @GetMapping("/api/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @AuditLog(operation = "VIEW", resourceType = "REVIEW", description = "查看评价统计信息")
    public ApiResponse<Object> getReviewStatistics() {
        try {
            Object statistics = reviewService.getReviewStatistics();
            return ApiResponse.success(statistics);
        } catch (Exception e) {
            return ApiResponse.error("获取评价统计信息失败：" + e.getMessage());
        }
    }
    
    // 获取最近评价
    @GetMapping("/api/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getRecentReviews(@RequestParam(defaultValue = "10") int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit);
            Page<Review> reviews = reviewService.getAllReviews(pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", reviews.getTotalElements());
            result.put("list", reviews.getContent());
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("获取最近评价失败：" + e.getMessage());
        }
    }
}




