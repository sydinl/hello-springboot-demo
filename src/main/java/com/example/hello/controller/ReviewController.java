package com.example.hello.controller;

import com.example.hello.common.RateLimit;
import com.example.hello.entity.Review;
import com.example.hello.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RateLimit(maxRequests = 200, timeWindow = 60, message = "评价API调用频率过高，请稍后再试")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    // 获取项目评价列表
    @GetMapping("/projectReviews")
    public ResponseEntity<Page<Review>> getProjectReviews(
            @RequestParam UUID projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewService.getProjectReviews(projectId, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    // 获取用户评价列表
    @GetMapping("/userReviews")
    public ResponseEntity<Page<Review>> getUserReviews(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewService.getUserReviews(userId, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    // 添加评价 - 严格限流
    @PostMapping("/add")
    @RateLimit(maxRequests = 10, timeWindow = 300, perUser = true, message = "评价提交过于频繁，请5分钟后再试")
    public ResponseEntity<Review> addReview(@RequestBody Review review) {
        Review newReview = reviewService.addReview(review);
        return ResponseEntity.ok(newReview);
    }
    
    // 回复评价
    @PostMapping("/reply")
    public ResponseEntity<Review> replyToReview(
            @RequestParam UUID reviewId,
            @RequestBody String replyContent) {
        Review updatedReview = reviewService.replyToReview(reviewId, replyContent);
        return ResponseEntity.ok(updatedReview);
    }
}