package com.example.hello.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.hello.entity.ProjectCategory;
import com.example.hello.repository.ProjectCategoryRepository;
import com.example.hello.service.ProjectCategoryService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectCategoryServiceImpl implements ProjectCategoryService {
    
    @Autowired
    private ProjectCategoryRepository projectCategoryRepository;
    
    @Override
    public List<ProjectCategory> getAllCategories() {
        try {
            log.info("开始查询所有项目分类");
            List<ProjectCategory> categories = projectCategoryRepository.findAllOrderByProjectCountDesc();
            log.info("成功查询到 {} 个分类", categories.size());
            return categories;
        } catch (Exception e) {
            log.error("查询所有项目分类失败", e);
            throw new RuntimeException("查询项目分类失败", e);
        }
    }
    
    @Override
    public ProjectCategory getCategoryDetail(String categoryId) {
        try {
            log.info("开始查询分类详情，分类ID：{}", categoryId);
            Optional<ProjectCategory> categoryOpt = projectCategoryRepository.findById(categoryId);
            
            if (categoryOpt.isPresent()) {
                ProjectCategory category = categoryOpt.get();
                log.info("成功查询到分类：{}", category.getName());
                return category;
            } else {
                log.warn("未找到分类，分类ID：{}", categoryId);
                throw new RuntimeException("分类不存在，分类ID：" + categoryId);
            }
        } catch (Exception e) {
            log.error("查询分类详情失败，分类ID：{}", categoryId, e);
            throw new RuntimeException("查询分类详情失败", e);
        }
    }
    
    @Override
    public Optional<ProjectCategory> findByName(String name) {
        try {
            log.info("开始根据名称查找分类，分类名称：{}", name);
            Optional<ProjectCategory> category = projectCategoryRepository.findByName(name);
            if (category.isPresent()) {
                log.info("成功找到分类：{}", category.get().getName());
            } else {
                log.warn("未找到分类，分类名称：{}", name);
            }
            return category;
        } catch (Exception e) {
            log.error("根据名称查找分类失败，分类名称：{}", name, e);
            throw new RuntimeException("查找分类失败", e);
        }
    }
    
    @Override
    public List<ProjectCategory> getHotCategories(Integer minProjectCount) {
        try {
            log.info("开始查询热门分类，最小项目数量：{}", minProjectCount);
            List<ProjectCategory> categories = projectCategoryRepository.findByProjectCountGreaterThan(minProjectCount);
            log.info("成功查询到 {} 个热门分类", categories.size());
            return categories;
        } catch (Exception e) {
            log.error("查询热门分类失败，最小项目数量：{}", minProjectCount, e);
            throw new RuntimeException("查询热门分类失败", e);
        }
    }
    
    // ========== 分类管理接口实现 ==========
    
    @Override
    public ProjectCategory createCategory(ProjectCategory category) {
        try {
            log.info("开始创建分类，分类名称：{}", category.getName());
            
            // 检查分类名称是否已存在
            Optional<ProjectCategory> existingCategory = projectCategoryRepository.findByName(category.getName());
            if (existingCategory.isPresent()) {
                throw new RuntimeException("分类名称已存在：" + category.getName());
            }
            
            // 设置ID
            if (category.getId() == null || category.getId().isEmpty()) {
                category.setId(UUID.randomUUID().toString());
            }
            
            // 设置默认值
            if (category.getProjectCount() == null) {
                category.setProjectCount(0);
            }
            
            ProjectCategory savedCategory = projectCategoryRepository.save(category);
            log.info("成功创建分类，分类ID：{}，分类名称：{}", savedCategory.getId(), savedCategory.getName());
            return savedCategory;
        } catch (Exception e) {
            log.error("创建分类失败，分类名称：{}", category.getName(), e);
            throw new RuntimeException("创建分类失败", e);
        }
    }
    
    @Override
    public ProjectCategory updateCategory(ProjectCategory category) {
        try {
            log.info("开始更新分类，分类ID：{}，分类名称：{}", category.getId(), category.getName());
            
            // 检查分类是否存在
            Optional<ProjectCategory> existingCategoryOpt = projectCategoryRepository.findById(category.getId());
            if (!existingCategoryOpt.isPresent()) {
                throw new RuntimeException("分类不存在，分类ID：" + category.getId());
            }
            
            // 检查名称是否与其他分类重复
            Optional<ProjectCategory> nameCheck = projectCategoryRepository.findByName(category.getName());
            if (nameCheck.isPresent() && !nameCheck.get().getId().equals(category.getId())) {
                throw new RuntimeException("分类名称已存在：" + category.getName());
            }
            
            ProjectCategory updatedCategory = projectCategoryRepository.save(category);
            log.info("成功更新分类，分类ID：{}，分类名称：{}", updatedCategory.getId(), updatedCategory.getName());
            return updatedCategory;
        } catch (Exception e) {
            log.error("更新分类失败，分类ID：{}", category.getId(), e);
            throw new RuntimeException("更新分类失败", e);
        }
    }
    
    @Override
    public void deleteCategory(String categoryId) {
        try {
            log.info("开始删除分类，分类ID：{}", categoryId);
            
            // 检查分类是否存在
            Optional<ProjectCategory> categoryOpt = projectCategoryRepository.findById(categoryId);
            if (!categoryOpt.isPresent()) {
                throw new RuntimeException("分类不存在，分类ID：" + categoryId);
            }
            
            // 检查分类下是否有项目
            ProjectCategory category = categoryOpt.get();
            if (category.getProjectCount() != null && category.getProjectCount() > 0) {
                throw new RuntimeException("该分类下还有项目，无法删除");
            }
            
            projectCategoryRepository.deleteById(categoryId);
            log.info("成功删除分类，分类ID：{}", categoryId);
        } catch (Exception e) {
            log.error("删除分类失败，分类ID：{}", categoryId, e);
            throw new RuntimeException("删除分类失败", e);
        }
    }
    
    @Override
    public void batchDeleteCategories(List<String> categoryIds) {
        try {
            log.info("开始批量删除分类，分类ID列表：{}", categoryIds);
            
            for (String categoryId : categoryIds) {
                // 检查分类是否存在
                Optional<ProjectCategory> categoryOpt = projectCategoryRepository.findById(categoryId);
                if (!categoryOpt.isPresent()) {
                    log.warn("分类不存在，跳过删除，分类ID：{}", categoryId);
                    continue;
                }
                
                // 检查分类下是否有项目
                ProjectCategory category = categoryOpt.get();
                if (category.getProjectCount() != null && category.getProjectCount() > 0) {
                    log.warn("分类下还有项目，跳过删除，分类ID：{}，项目数量：{}", categoryId, category.getProjectCount());
                    continue;
                }
                
                projectCategoryRepository.deleteById(categoryId);
                log.info("成功删除分类，分类ID：{}", categoryId);
            }
            
            log.info("批量删除分类完成");
        } catch (Exception e) {
            log.error("批量删除分类失败", e);
            throw new RuntimeException("批量删除分类失败", e);
        }
    }
}