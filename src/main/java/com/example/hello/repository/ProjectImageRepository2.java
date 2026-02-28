package com.example.hello.repository;

import com.example.hello.entity.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectImageRepository2 extends JpaRepository<ProjectImage, UUID> {
    // 根据项目ID查询所有图片
    List<ProjectImage> findByProjectId(UUID projectId);
}