package com.example.hello.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.example.hello.entity.ProjectCategory;

@Service
public interface ProjectCategoryService {
    // 获取所有项目分类
    List<ProjectCategory> getAllCategories();
    
    // 获取分类详情
    ProjectCategory getCategoryDetail(String categoryId);
    
    // 根据名称查找分类
    Optional<ProjectCategory> findByName(String name);
    
    // 获取热门分类（项目数量大于指定值）
    List<ProjectCategory> getHotCategories(Integer minProjectCount);
    
    // ========== 分类管理接口 ==========
    
    // 创建分类
    ProjectCategory createCategory(ProjectCategory category);
    
    // 更新分类
    ProjectCategory updateCategory(ProjectCategory category);
    
    // 删除分类
    void deleteCategory(String categoryId);
    
    // 批量删除分类
    void batchDeleteCategories(List<String> categoryIds);
}