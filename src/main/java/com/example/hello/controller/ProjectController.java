package com.example.hello.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.hello.annotation.AuditLog;
import com.example.hello.common.ApiResponse;
import com.example.hello.common.RateLimit;
import com.example.hello.entity.Project;
import com.example.hello.entity.ProjectCategory;
import com.example.hello.service.ProjectCategoryService;
import com.example.hello.service.ProjectService;
import com.example.hello.util.CloudStorageUtil;

@RestController
@RequestMapping("/api/projects")
@RateLimit(maxRequests = 500, timeWindow = 60, message = "项目API调用频率过高，请稍后再试")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private ProjectCategoryService projectCategoryService;
    
    /**
     * 转换项目列表中的云开发 FileID 为 HTTPS URL（用于网页端展示）
     */
    private void convertProjectImageUrls(List<Project> projects) {
        if (projects != null) {
            projects.forEach(project -> {
                if (project.getImage() != null) {
                    project.setImage(CloudStorageUtil.convertFileIdToUrl(project.getImage()));
                }
            });
        }
    }
    
    // 1.1 获取项目列表 - 支持分类筛选和分页（只返回启用状态的项目）
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getProjectList(
            @RequestParam(required = false) String category,  // 项目分类名称，可选
            @RequestParam(required = false) String categoryId,  // 项目分类ID，可选
            @RequestParam(defaultValue = "1") int page,       // 页码，默认1
            @RequestParam(defaultValue = "10") int pageSize,  // 每页数量，默认10
            @RequestParam(required = false) String search) {   // 搜索关键词，可选
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        
        Page<Project> projects;
        if (categoryId != null && !categoryId.isEmpty()) {
            // 优先使用分类ID查询（只返回启用状态）
            projects = projectService.getActiveProjectsByCategoryId(categoryId, pageable);
        } else if (category != null && !category.isEmpty() && !"all".equals(category)) {
            // 使用分类名称查询（只返回启用状态）
            projects = projectService.getActiveProjectsByCategory(category, pageable);
        } else if (search != null && !search.isEmpty()) {
            // 搜索项目（只返回启用状态）
            projects = projectService.searchActiveProjects(search, pageable);
        } else {
            // 获取所有项目（只返回启用状态）
            projects = projectService.getActiveProjectList(pageable);
        }
        
        // 转换云开发 FileID 为 HTTPS URL（用于网页端展示）
        List<Project> projectList = projects.getContent();
        convertProjectImageUrls(projectList);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", projects.getTotalElements());
        result.put("list", projectList);
        
        return ApiResponse.success(result);
    }
    
    // 1.2 获取项目分类列表
    @GetMapping("/categories")
    public ApiResponse<List<ProjectCategory>> getCategories() {
        List<ProjectCategory> categories = projectCategoryService.getAllCategories();
        return ApiResponse.success(categories);
    }
    
    // 1.3 根据分类ID获取项目列表
    @GetMapping("/category/{categoryId}")
    public ApiResponse<Map<String, Object>> getProjectsByCategoryId(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Project> projects = projectService.getProjectsByCategoryId(categoryId, pageable);
        
        // 转换云开发 FileID 为 HTTPS URL
        List<Project> projectList = projects.getContent();
        convertProjectImageUrls(projectList);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", projects.getTotalElements());
        result.put("list", projectList);
        result.put("categoryId", categoryId);
        
        return ApiResponse.success(result);
    }
    
    // 1.3 获取热门项目列表（只返回启用状态的项目）
    @GetMapping("/hot")
    public ApiResponse<Map<String, Object>> getHotProjects(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        // 热门项目也需要是启用状态的
        Page<Project> hotProjects = projectService.getActiveHotProjects(pageable);
        
        // 转换云开发 FileID 为 HTTPS URL
        List<Project> hotProjectList = hotProjects.getContent();
        convertProjectImageUrls(hotProjectList);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", hotProjects.getTotalElements());
        result.put("list", hotProjectList);
        
        return ApiResponse.success(result);
    }
    
    // 1.4 获取推荐项目列表（只返回启用状态的项目）
    @GetMapping("/recommend")
    public ApiResponse<Map<String, Object>> getRecommendProjects(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        // 推荐项目也需要是启用状态的
        Page<Project> recommendProjects = projectService.getActiveRecommendProjects(pageable);
        
        // 转换云开发 FileID 为 HTTPS URL
        List<Project> recommendProjectList = recommendProjects.getContent();
        convertProjectImageUrls(recommendProjectList);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", recommendProjects.getTotalElements());
        result.put("list", recommendProjectList);
        
        return ApiResponse.success(result);
    }
    
    // 获取项目详情
    @GetMapping("/detail")
    public ApiResponse<Project> getProjectDetail(@RequestParam String projectId) {
        try {
            Project project = projectService.getProjectDetail(projectId);
            // 转换云开发 FileID 为 HTTPS URL
            if (project.getImage() != null) {
                project.setImage(CloudStorageUtil.convertFileIdToUrl(project.getImage()));
            }
            return ApiResponse.success(project);
        } catch (Exception e) {
            return ApiResponse.error("获取项目详情失败：" + e.getMessage());
        }
    }
    
    // ========== 项目管理接口 ==========
    
    // 创建项目
    @PostMapping("/create")
    @AuditLog(operation = "CREATE", resourceType = "PROJECT", description = "创建项目")
    public ApiResponse<Project> createProject(@RequestBody Project project) {
        Project createdProject = projectService.createProject(project);
        return ApiResponse.success(createdProject);
    }
    
    // 更新项目
    @PutMapping("/update/{projectId}")
    @AuditLog(operation = "UPDATE", resourceType = "PROJECT", description = "更新项目")
    public ApiResponse<Project> updateProject(@PathVariable String projectId, @RequestBody Project project) {
        Project updatedProject = projectService.updateProject(projectId, project);
        return ApiResponse.success(updatedProject);
    }
    
    // 删除项目
    @DeleteMapping("/delete/{projectId}")
    @AuditLog(operation = "DELETE", resourceType = "PROJECT", description = "删除项目")
    public ApiResponse<String> deleteProject(@PathVariable String projectId) {
        projectService.deleteProject(projectId);
        return ApiResponse.success("删除项目成功");
    }
    
    // 批量删除项目
    @DeleteMapping("/batch-delete")
    @AuditLog(operation = "DELETE", resourceType = "PROJECT", description = "批量删除项目")
    public ApiResponse<String> deleteProjects(@RequestBody String[] projectIds) {
        projectService.deleteProjects(projectIds);
        return ApiResponse.success("批量删除项目成功");
    }
    
    // 设置项目为热门
    @PutMapping("/set-hot/{projectId}")
    @AuditLog(operation = "UPDATE", resourceType = "PROJECT", description = "设置项目热门状态")
    public ApiResponse<Project> setProjectHot(@PathVariable String projectId, @RequestParam Boolean isHot) {
        Project updatedProject = projectService.setProjectHot(projectId, isHot);
        return ApiResponse.success(updatedProject);
    }
    
    // 设置项目推荐
    @PutMapping("/set-recommend/{projectId}")
    @AuditLog(operation = "UPDATE", resourceType = "PROJECT", description = "设置项目推荐状态")
    public ApiResponse<Project> setProjectRecommend(@PathVariable String projectId, @RequestParam Boolean isRecommend) {
        Project updatedProject = projectService.setProjectRecommend(projectId, isRecommend);
        return ApiResponse.success(updatedProject);
    }
    
    // 更新项目状态
    @PutMapping("/update-status/{projectId}")
    @AuditLog(operation = "UPDATE", resourceType = "PROJECT", description = "更新项目状态")
    public ApiResponse<Project> updateProjectStatus(@PathVariable String projectId, @RequestParam String status) {
        Project updatedProject = projectService.updateProjectStatus(projectId, status);
        return ApiResponse.success(updatedProject);
    }
    
    // 获取项目统计信息
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getProjectStatistics() {
        Map<String, Object> statistics = projectService.getProjectStatistics();
        return ApiResponse.success(statistics);
    }
    
    // 按状态获取项目列表
    @GetMapping("/by-status")
    public ApiResponse<Map<String, Object>> getProjectsByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Project> projects = projectService.getProjectsByStatus(status, pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", projects.getTotalElements());
        result.put("list", projects.getContent());
        
        return ApiResponse.success(result);
    }
    
    // 按价格范围获取项目列表
    @GetMapping("/by-price-range")
    public ApiResponse<Map<String, Object>> getProjectsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Project> projects = projectService.getProjectsByPriceRange(minPrice, maxPrice, pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", projects.getTotalElements());
        result.put("list", projects.getContent());
        
        return ApiResponse.success(result);
    }
    
    // 获取项目销售排行（只返回启用状态的项目）
    @GetMapping("/sales-ranking")
    public ApiResponse<Map<String, Object>> getProjectSalesRanking(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Project> projects = projectService.getActiveProjectSalesRanking(pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", projects.getTotalElements());
        result.put("list", projects.getContent());
        
        return ApiResponse.success(result);
    }
    
    // 获取项目评分排行（只返回启用状态的项目）
    @GetMapping("/rating-ranking")
    public ApiResponse<Map<String, Object>> getProjectRatingRanking(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Project> projects = projectService.getActiveProjectRatingRanking(pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", projects.getTotalElements());
        result.put("list", projects.getContent());
        
        return ApiResponse.success(result);
    }
    
    // 获取所有项目（管理用）
    @GetMapping("/admin/all")
    @AuditLog(operation = "VIEW", resourceType = "PROJECT", description = "查看项目列表（管理）")
    public ApiResponse<Map<String, Object>> getAllProjects(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Project> projects;
        
        if (status != null && !status.isEmpty()) {
            projects = projectService.getProjectsByStatus(status, pageable);
        } else if (categoryId != null && !categoryId.isEmpty()) {
            // 优先使用分类ID查询
            projects = projectService.getProjectsByCategoryId(categoryId, pageable);
        } else if (category != null && !category.isEmpty()) {
            // 使用分类名称查询
            projects = projectService.getProjectsByCategory(category, pageable);
        } else if (search != null && !search.isEmpty()) {
            projects = projectService.searchProjects(search, pageable);
        } else {
            projects = projectService.getProjectList(pageable);
        }
        
        // 转换云开发 FileID 为 HTTPS URL（用于网页端展示）
        List<Project> projectList = projects.getContent();
        projectList.forEach(project -> {
            if (project.getImage() != null) {
                project.setImage(CloudStorageUtil.convertFileIdToUrl(project.getImage()));
            }
        });
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", projects.getTotalElements());
        result.put("list", projectList);
        
        return ApiResponse.success(result);
    }
}