package com.example.hello.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.hello.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    // 按分类ID分页查询项目
    Page<Project> findByCategory(String category, Pageable pageable);
    
    // 按分类ID分页查询项目
    Page<Project> findByCategoryId(String categoryId, Pageable pageable);
    
    // 按名称模糊查询项目
    Page<Project> findByNameContaining(String keyword, Pageable pageable);
    
    // 只查询启用状态的项目 - 按分类
    Page<Project> findByCategoryAndStatus(String category, String status, Pageable pageable);
    
    // 只查询启用状态的项目 - 按分类ID
    Page<Project> findByCategoryIdAndStatus(String categoryId, String status, Pageable pageable);
    
    // 只查询启用状态的项目 - 按名称模糊查询
    Page<Project> findByNameContainingAndStatus(String keyword, String status, Pageable pageable);
    
    // 只查询启用状态的项目 - 全部
    Page<Project> findByStatus(String status, Pageable pageable);
    
    // 按价格范围查询项目
    @Query("SELECT p FROM Project p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Project> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);
    
    // 按热门状态查询项目
    Page<Project> findByIsHot(Boolean isHot, Pageable pageable);
    
    // 按推荐状态查询项目
    Page<Project> findByIsRecommend(Boolean isRecommend, Pageable pageable);
    
    // 按热门状态和启用状态查询项目
    Page<Project> findByIsHotAndStatus(Boolean isHot, String status, Pageable pageable);
    
    // 按推荐状态和启用状态查询项目
    Page<Project> findByIsRecommendAndStatus(Boolean isRecommend, String status, Pageable pageable);
    
    // 按销售数量排序查询项目
    Page<Project> findAllByOrderBySalesCountDesc(Pageable pageable);
    
    // 按评分排序查询项目
    Page<Project> findAllByOrderByRatingDesc(Pageable pageable);
    
    // 按销售数量排序查询启用状态的项目
    Page<Project> findByStatusOrderBySalesCountDesc(String status, Pageable pageable);
    
    // 按评分排序查询启用状态的项目
    Page<Project> findByStatusOrderByRatingDesc(String status, Pageable pageable);
    
    // 按创建时间排序查询项目
    Page<Project> findAllByOrderByCreateTimeDesc(Pageable pageable);
    
    // 统计项目总数
    @Query("SELECT COUNT(p) FROM Project p")
    Long countAllProjects();
    
    // 统计按状态分组的项目数量
    @Query("SELECT p.status, COUNT(p) FROM Project p GROUP BY p.status")
    Object[][] countProjectsByStatus();
    
    // 统计按分类分组的项目数量
    @Query("SELECT p.category, COUNT(p) FROM Project p GROUP BY p.category")
    Object[][] countProjectsByCategory();
    
    // 统计热门项目数量
    @Query("SELECT COUNT(p) FROM Project p WHERE p.isHot = true")
    Long countHotProjects();
    
    // 统计推荐项目数量
    @Query("SELECT COUNT(p) FROM Project p WHERE p.isRecommend = true")
    Long countRecommendProjects();
}