package com.example.hello.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.hello.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    // 按项目ID分页查询评价
    Page<Review> findByProjectId(UUID projectId, Pageable pageable);
    
    // 按用户ID分页查询评价
    Page<Review> findByUserId(UUID userId, Pageable pageable);
    
    // 按内容模糊查询评价
    @Query("SELECT r FROM Review r WHERE r.content LIKE %:keyword%")
    Page<Review> findByContentContaining(@Param("keyword") String keyword, Pageable pageable);
    
    // 按评分范围查询评价
    @Query("SELECT r FROM Review r WHERE r.rating BETWEEN :minRating AND :maxRating")
    Page<Review> findByRatingRange(@Param("minRating") Integer minRating, @Param("maxRating") Integer maxRating, Pageable pageable);
    
    // 按创建时间排序查询评价
    Page<Review> findAllByOrderByCreateTimeDesc(Pageable pageable);
    
    // 按评分排序查询评价
    Page<Review> findAllByOrderByRatingDesc(Pageable pageable);
    
    // 统计评价总数
    @Query("SELECT COUNT(r) FROM Review r")
    Long countAllReviews();
    
    // 统计按评分分组的评价数量
    @Query("SELECT r.rating, COUNT(r) FROM Review r GROUP BY r.rating ORDER BY r.rating")
    Object[][] countReviewsByRating();
    
    // 统计平均评分
    @Query("SELECT AVG(r.rating) FROM Review r")
    Double getAverageRating();
    
    // 统计最近30天的评价数量
    @Query("SELECT COUNT(r) FROM Review r WHERE r.createTime >= :thirtyDaysAgo")
    Long countRecentReviews(@Param("thirtyDaysAgo") java.util.Date thirtyDaysAgo);
}