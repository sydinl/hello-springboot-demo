package com.example.hello.service;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.hello.entity.Project;

@Service
public interface ProjectService {
    // 获取项目列表
    Page<Project> getProjectList(Pageable pageable);
    
    // 按分类获取项目列表
    Page<Project> getProjectsByCategory(String category, Pageable pageable);
    
    // 按分类ID获取项目列表
    Page<Project> getProjectsByCategoryId(String categoryId, Pageable pageable);
    
    // 搜索项目
    Page<Project> searchProjects(String keyword, Pageable pageable);
    
    // ========== 只返回启用状态项目的方法 ==========
    
    // 获取启用状态的项目列表
    Page<Project> getActiveProjectList(Pageable pageable);
    
    // 按分类获取启用状态的项目列表
    Page<Project> getActiveProjectsByCategory(String category, Pageable pageable);
    
    // 按分类ID获取启用状态的项目列表
    Page<Project> getActiveProjectsByCategoryId(String categoryId, Pageable pageable);
    
    // 搜索启用状态的项目
    Page<Project> searchActiveProjects(String keyword, Pageable pageable);
    
    // 获取项目详情
    Project getProjectDetail(String projectId);
    
    // 获取热门项目列表
    Page<Project> getHotProjects(Pageable pageable);
    
    // 获取推荐项目列表
    Page<Project> getRecommendProjects(Pageable pageable);
    
    // 获取启用状态的热门项目列表
    Page<Project> getActiveHotProjects(Pageable pageable);
    
    // 获取启用状态的推荐项目列表
    Page<Project> getActiveRecommendProjects(Pageable pageable);
    
    // ========== 项目管理接口 ==========
    
    // 创建项目
    Project createProject(Project project);
    
    // 更新项目
    Project updateProject(String projectId, Project project);
    
    // 删除项目
    void deleteProject(String projectId);
    
    // 批量删除项目
    void deleteProjects(String[] projectIds);
    
    // 设置项目为热门
    Project setProjectHot(String projectId, Boolean isHot);
    
    // 设置项目推荐
    Project setProjectRecommend(String projectId, Boolean isRecommend);
    
    // 更新项目状态
    Project updateProjectStatus(String projectId, String status);
    
    // 获取项目统计信息
    Map<String, Object> getProjectStatistics();
    
    // 按状态获取项目列表
    Page<Project> getProjectsByStatus(String status, Pageable pageable);
    
    // 按价格范围获取项目列表
    Page<Project> getProjectsByPriceRange(Double minPrice, Double maxPrice, Pageable pageable);
    
    // 获取项目销售排行
    Page<Project> getProjectSalesRanking(Pageable pageable);
    
    // 获取项目评分排行
    Page<Project> getProjectRatingRanking(Pageable pageable);
    
    // 获取启用状态的项目销售排行
    Page<Project> getActiveProjectSalesRanking(Pageable pageable);
    
    // 获取启用状态的项目评分排行
    Page<Project> getActiveProjectRatingRanking(Pageable pageable);
}