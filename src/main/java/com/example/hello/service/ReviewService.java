package com.example.hello.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.hello.entity.Review;

public interface ReviewService {
    // 获取项目的评价列表
    Page<Review> getProjectReviews(UUID projectId, Pageable pageable);
    
    // 获取用户的评价列表
    Page<Review> getUserReviews(UUID userId, Pageable pageable);
    
    // 添加评价
    Review addReview(Review review);
    
    // 回复评价
    Review replyToReview(UUID reviewId, String replyContent);
    
    // ========== 管理功能 ==========
    
    // 获取所有评价（管理用）
    Page<Review> getAllReviews(Pageable pageable);
    
    // 根据状态获取评价
    Page<Review> getReviewsByStatus(String status, Pageable pageable);
    
    // 搜索评价
    Page<Review> searchReviews(String keyword, Pageable pageable);
    
    // 获取评价详情
    Review getReviewDetail(String reviewId);
    
    // 删除评价
    void deleteReview(String reviewId);
    
    // 批量删除评价
    void deleteReviews(String[] reviewIds);
    
    // 更新评价状态
    Review updateReviewStatus(String reviewId, String status);
    
    // 获取评价统计信息
    Object getReviewStatistics();
}