package com.example.hello.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.hello.entity.ProjectImage;

@Repository
public interface ProjectImageRepository extends JpaRepository<ProjectImage, UUID> {
    // 根据项目ID查询所有图片
    List<ProjectImage> findByProjectId(UUID projectId);
}