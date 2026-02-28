package com.example.hello.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.hello.entity.Reply;
import com.example.hello.entity.Review;
import com.example.hello.repository.ReplyRepository;
import com.example.hello.repository.ReviewRepository;
import com.example.hello.service.ReviewService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ReplyRepository replyRepository;
    
    @Override
    public Page<Review> getProjectReviews(UUID projectId, Pageable pageable) {
        return reviewRepository.findByProjectId(projectId, pageable);
    }
    
    @Override
    public Page<Review> getUserReviews(UUID userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable);
    }
    
    @Override
    @Transactional
    public Review addReview(Review review) {
        try {
            Review savedReview = reviewRepository.save(review);
            log.info("添加评价成功，评价ID：{}", savedReview.getId());
            return savedReview;
        } catch (Exception e) {
            log.error("添加评价失败", e);
            throw new RuntimeException("添加评价失败", e);
        }
    }

    @Override
    @Transactional
    public Review replyToReview(UUID reviewId, String replyContent) {
        try {
            Review review = reviewRepository.findById(reviewId.toString())
                    .orElseThrow(() -> new RuntimeException("评价不存在，评价ID：" + reviewId));

            Reply reply = new Reply();
            reply.setId(UUID.randomUUID().toString());
            reply.setContent(replyContent);
            
            review.setReply(reply);
            Review updatedReview = reviewRepository.save(review);
            
            log.info("回复评价成功，评价ID：{}", reviewId);
            return updatedReview;
        } catch (Exception e) {
            log.error("回复评价失败，评价ID：{}", reviewId, e);
            throw new RuntimeException("回复评价失败", e);
        }
    }
    
    // ========== 管理功能实现 ==========
    
    @Override
    public Page<Review> getAllReviews(Pageable pageable) {
        try {
            return reviewRepository.findAllByOrderByCreateTimeDesc(pageable);
        } catch (Exception e) {
            log.error("获取评价列表失败", e);
            throw new RuntimeException("获取评价列表失败", e);
        }
    }
    
    @Override
    public Page<Review> getReviewsByStatus(String status, Pageable pageable) {
        // 这里可以根据实际需求实现状态筛选
        // 目前返回所有评价
        return getAllReviews(pageable);
    }
    
    @Override
    public Page<Review> searchReviews(String keyword, Pageable pageable) {
        try {
            return reviewRepository.findByContentContaining(keyword, pageable);
        } catch (Exception e) {
            log.error("搜索评价失败，关键词：{}", keyword, e);
            throw new RuntimeException("搜索评价失败", e);
        }
    }
    
    @Override
    public Review getReviewDetail(String reviewId) {
        try {
            return reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new RuntimeException("评价不存在，评价ID：" + reviewId));
        } catch (Exception e) {
            log.error("获取评价详情失败，评价ID：{}", reviewId, e);
            throw new RuntimeException("获取评价详情失败", e);
        }
    }
    
    @Override
    @Transactional
    public void deleteReview(String reviewId) {
        try {
            if (!reviewRepository.existsById(reviewId)) {
                throw new RuntimeException("评价不存在，评价ID：" + reviewId);
            }
            
            reviewRepository.deleteById(reviewId);
            log.info("删除评价成功，评价ID：{}", reviewId);
            
        } catch (Exception e) {
            log.error("删除评价失败，评价ID：{}", reviewId, e);
            throw new RuntimeException("删除评价失败", e);
        }
    }
    
    @Override
    @Transactional
    public void deleteReviews(String[] reviewIds) {
        try {
            for (String reviewId : reviewIds) {
                if (reviewRepository.existsById(reviewId)) {
                    reviewRepository.deleteById(reviewId);
                }
            }
            log.info("批量删除评价成功，删除数量：{}", reviewIds.length);
            
        } catch (Exception e) {
            log.error("批量删除评价失败", e);
            throw new RuntimeException("批量删除评价失败", e);
        }
    }
    
    @Override
    @Transactional
    public Review updateReviewStatus(String reviewId, String status) {
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new RuntimeException("评价不存在，评价ID：" + reviewId));
            
            // 这里可以根据实际需求添加状态字段
            // 目前直接返回原评价
            Review updatedReview = reviewRepository.save(review);
            
            log.info("更新评价状态成功，评价ID：{}，新状态：{}", reviewId, status);
            return updatedReview;
            
        } catch (Exception e) {
            log.error("更新评价状态失败，评价ID：{}", reviewId, e);
            throw new RuntimeException("更新评价状态失败", e);
        }
    }
    
    @Override
    public Object getReviewStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总评价数
            Long totalReviews = reviewRepository.countAllReviews();
            statistics.put("totalReviews", totalReviews);
            
            // 平均评分
            Double averageRating = reviewRepository.getAverageRating();
            statistics.put("averageRating", averageRating != null ? averageRating : 0.0);
            
            // 最近30天评价数
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.add(java.util.Calendar.DAY_OF_MONTH, -30);
            java.util.Date thirtyDaysAgo = calendar.getTime();
            Long recentReviews = reviewRepository.countRecentReviews(thirtyDaysAgo);
            statistics.put("recentReviews", recentReviews);
            
            // 按评分分组统计
            Object[][] ratingStats = reviewRepository.countReviewsByRating();
            Map<Integer, Long> ratingMap = new HashMap<>();
            for (Object[] stat : ratingStats) {
                ratingMap.put((Integer) stat[0], (Long) stat[1]);
            }
            statistics.put("ratingStatistics", ratingMap);
            
            return statistics;
            
        } catch (Exception e) {
            log.error("获取评价统计信息失败", e);
            throw new RuntimeException("获取评价统计信息失败", e);
        }
    }
}