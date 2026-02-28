package com.example.hello.controller;

import com.example.hello.entity.ProjectCategory;
import com.example.hello.service.ProjectCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class ProjectCategoryController {
    
    @Autowired
    private ProjectCategoryService projectCategoryService;
    
    // 获取所有项目分类
    @GetMapping("/all")
    public ResponseEntity<List<ProjectCategory>> getAllCategories() {
        List<ProjectCategory> categories = projectCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    // 获取分类详情
    @GetMapping("/detail")
    public ResponseEntity<ProjectCategory> getCategoryDetail(@RequestParam String categoryId) {
        ProjectCategory category = projectCategoryService.getCategoryDetail(categoryId);
        return ResponseEntity.ok(category);
    }
}