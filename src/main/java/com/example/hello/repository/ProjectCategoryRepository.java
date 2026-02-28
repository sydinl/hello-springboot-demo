package com.example.hello.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.hello.entity.ProjectCategory;

@Repository
public interface ProjectCategoryRepository extends JpaRepository<ProjectCategory, String> {
    
    // 根据名称查找分类
    Optional<ProjectCategory> findByName(String name);
    
    // 查找所有分类，按项目数量降序排列
    @Query("SELECT pc FROM ProjectCategory pc ORDER BY pc.projectCount DESC")
    List<ProjectCategory> findAllOrderByProjectCountDesc();
    
    // 查找项目数量大于指定值的分类
    List<ProjectCategory> findByProjectCountGreaterThan(Integer projectCount);
}