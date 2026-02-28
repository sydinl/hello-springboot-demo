package com.example.hello.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.hello.annotation.AuditLog;
import com.example.hello.common.ApiResponse;
import com.example.hello.entity.ProjectCategory;
import com.example.hello.service.ProjectCategoryService;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin/categories")
@Slf4j
public class ProjectCategoryAdminController {
    
    @Autowired
    private ProjectCategoryService projectCategoryService;
    
    // 分类管理页面
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String categoryManagementPage(Model model) {
        log.info("访问分类管理页面");
        return "admin/category-management";
    }
    
    // ========== 分类管理API接口 ==========
    
    // 获取所有分类（分页）
    @GetMapping("/api/list")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    @AuditLog(operation = "VIEW", resourceType = "CATEGORY", description = "查看分类列表")
    public ApiResponse<Map<String, Object>> getCategoryList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            log.info("获取分类列表，页码：{}，每页数量：{}，搜索：{}", page, pageSize, search);
            
            // 创建分页和排序对象
            Sort sort = sortDir.equals("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
            
            // 获取分类列表
            List<ProjectCategory> allCategories = projectCategoryService.getAllCategories();
            
            // 简单的分页实现（因为当前没有分页查询方法）
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), allCategories.size());
            
            List<ProjectCategory> categories = allCategories.subList(start, end);
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", allCategories.size());
            result.put("list", categories);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("totalPages", (int) Math.ceil((double) allCategories.size() / pageSize));
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return ApiResponse.error("获取分类列表失败：" + e.getMessage());
        }
    }
    
    // 获取分类详情
    @GetMapping("/api/detail")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    @AuditLog(operation = "VIEW", resourceType = "CATEGORY", description = "查看分类详情")
    public ApiResponse<ProjectCategory> getCategoryDetail(@RequestParam String categoryId) {
        try {
            log.info("获取分类详情，分类ID：{}", categoryId);
            ProjectCategory category = projectCategoryService.getCategoryDetail(categoryId);
            return ApiResponse.success(category);
        } catch (Exception e) {
            log.error("获取分类详情失败，分类ID：{}", categoryId, e);
            return ApiResponse.error("获取分类详情失败：" + e.getMessage());
        }
    }
    
    // 创建分类
    @PostMapping("/api/create")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    @AuditLog(operation = "CREATE", resourceType = "CATEGORY", description = "创建分类")
    public ApiResponse<ProjectCategory> createCategory(@RequestBody ProjectCategory category) {
        try {
            log.info("创建分类，分类名称：{}", category.getName());
            
            // 检查分类名称是否已存在
            Optional<ProjectCategory> existingCategory = projectCategoryService.findByName(category.getName());
            if (existingCategory.isPresent()) {
                return ApiResponse.error("分类名称已存在");
            }
            
            // 设置默认值
            if (category.getProjectCount() == null) {
                category.setProjectCount(0);
            }
            
            ProjectCategory savedCategory = projectCategoryService.createCategory(category);
            return ApiResponse.success(savedCategory);
        } catch (Exception e) {
            log.error("创建分类失败", e);
            return ApiResponse.error("创建分类失败：" + e.getMessage());
        }
    }
    
    // 更新分类
    @PutMapping("/api/update/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    @AuditLog(operation = "UPDATE", resourceType = "CATEGORY", description = "更新分类")
    public ApiResponse<ProjectCategory> updateCategory(
            @PathVariable String categoryId, 
            @RequestBody ProjectCategory category) {
        try {
            log.info("更新分类，分类ID：{}，分类名称：{}", categoryId, category.getName());
            
            // 检查分类是否存在
            ProjectCategory existingCategory = projectCategoryService.getCategoryDetail(categoryId);
            
            // 检查名称是否与其他分类重复
            Optional<ProjectCategory> nameCheck = projectCategoryService.findByName(category.getName());
            if (nameCheck.isPresent() && !nameCheck.get().getId().equals(categoryId)) {
                return ApiResponse.error("分类名称已存在");
            }
            
            category.setId(categoryId);
            ProjectCategory updatedCategory = projectCategoryService.updateCategory(category);
            return ApiResponse.success(updatedCategory);
        } catch (Exception e) {
            log.error("更新分类失败，分类ID：{}", categoryId, e);
            return ApiResponse.error("更新分类失败：" + e.getMessage());
        }
    }
    
    // 删除分类
    @DeleteMapping("/api/delete/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    @AuditLog(operation = "DELETE", resourceType = "CATEGORY", description = "删除分类")
    public ApiResponse<Void> deleteCategory(@PathVariable String categoryId) {
        try {
            log.info("删除分类，分类ID：{}", categoryId);
            projectCategoryService.deleteCategory(categoryId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("删除分类失败，分类ID：{}", categoryId, e);
            return ApiResponse.error("删除分类失败：" + e.getMessage());
        }
    }
    
    // 批量删除分类
    @DeleteMapping("/api/batch-delete")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    @AuditLog(operation = "DELETE", resourceType = "CATEGORY", description = "批量删除分类")
    public ApiResponse<Void> batchDeleteCategories(@RequestBody List<String> categoryIds) {
        try {
            log.info("批量删除分类，分类ID列表：{}", categoryIds);
            projectCategoryService.batchDeleteCategories(categoryIds);
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("批量删除分类失败", e);
            return ApiResponse.error("批量删除分类失败：" + e.getMessage());
        }
    }
    
    // 获取热门分类
    @GetMapping("/api/hot")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ApiResponse<List<ProjectCategory>> getHotCategories(
            @RequestParam(defaultValue = "5") Integer minProjectCount) {
        try {
            log.info("获取热门分类，最小项目数量：{}", minProjectCount);
            List<ProjectCategory> categories = projectCategoryService.getHotCategories(minProjectCount);
            return ApiResponse.success(categories);
        } catch (Exception e) {
            log.error("获取热门分类失败", e);
            return ApiResponse.error("获取热门分类失败：" + e.getMessage());
        }
    }
}
