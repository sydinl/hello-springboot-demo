package com.example.hello.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.hello.entity.Project;
import com.example.hello.repository.ProjectRepository;
import com.example.hello.service.ProjectService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Override
    public Page<Project> getProjectList(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }
    
    @Override
    public Page<Project> getProjectsByCategory(String category, Pageable pageable) {
        return projectRepository.findByCategory(category, pageable);
    }
    
    @Override
    public Page<Project> getProjectsByCategoryId(String categoryId, Pageable pageable) {
        return projectRepository.findByCategoryId(categoryId, pageable);
    }
    
    @Override
    public Page<Project> searchProjects(String keyword, Pageable pageable) {
        return projectRepository.findByNameContaining(keyword, pageable);
    }
    
    // ========== 只返回启用状态项目的方法 ==========
    
    @Override
    public Page<Project> getActiveProjectList(Pageable pageable) {
        return projectRepository.findByStatus("active", pageable);
    }
    
    @Override
    public Page<Project> getActiveProjectsByCategory(String category, Pageable pageable) {
        return projectRepository.findByCategoryAndStatus(category, "active", pageable);
    }
    
    @Override
    public Page<Project> getActiveProjectsByCategoryId(String categoryId, Pageable pageable) {
        return projectRepository.findByCategoryIdAndStatus(categoryId, "active", pageable);
    }
    
    @Override
    public Page<Project> searchActiveProjects(String keyword, Pageable pageable) {
        return projectRepository.findByNameContainingAndStatus(keyword, "active", pageable);
    }
    
    @Override
    public Project getProjectDetail(String projectId) {
        try {
            return projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("项目不存在，项目ID：" + projectId));
        } catch (Exception e) {
            log.error("获取项目详情失败，项目ID：{}", projectId, e);
            throw new RuntimeException("获取项目详情失败", e);
        }
    }
    
    @Override
    public Page<Project> getHotProjects(Pageable pageable) {
        return projectRepository.findByIsHot(true, pageable);
    }
    
    @Override
    public Page<Project> getRecommendProjects(Pageable pageable) {
        return projectRepository.findByIsRecommend(true, pageable);
    }
    
    @Override
    public Page<Project> getActiveHotProjects(Pageable pageable) {
        return projectRepository.findByIsHotAndStatus(true, "active", pageable);
    }
    
    @Override
    public Page<Project> getActiveRecommendProjects(Pageable pageable) {
        return projectRepository.findByIsRecommendAndStatus(true, "active", pageable);
    }
    
    // ========== 项目管理接口实现 ==========
    
    @Override
    @Transactional
    public Project createProject(Project project) {
        try {
            // 设置默认值
            if (project.getStatus() == null) {
                project.setStatus("active");
            }
            if (project.getIsHot() == null) {
                project.setIsHot(false);
            }
            if (project.getIsRecommend() == null) {
                project.setIsRecommend(false);
            }
            if (project.getSalesCount() == null) {
                project.setSalesCount(0);
            }
            if (project.getRating() == null) {
                project.setRating(0.0);
            }
            
            Project savedProject = projectRepository.save(project);
            log.info("创建项目成功，项目ID：{}", savedProject.getId());
            return savedProject;
            
        } catch (Exception e) {
            log.error("创建项目失败", e);
            throw new RuntimeException("创建项目失败", e);
        }
    }
    
    @Override
    @Transactional
    public Project updateProject(String projectId, Project project) {
        try {
            Project existingProject = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("项目不存在，项目ID：" + projectId));
            
            // 更新项目信息
            existingProject.setName(project.getName());
            existingProject.setDescription(project.getDescription());
            existingProject.setPrice(project.getPrice());
            existingProject.setImage(project.getImage());
            existingProject.setDuration(project.getDuration());
            existingProject.setCategory(project.getCategory());
            existingProject.setCategoryId(project.getCategoryId());
            existingProject.setDetails(project.getDetails());
            existingProject.setStatus(project.getStatus());
            existingProject.setIsHot(project.getIsHot());
            existingProject.setIsRecommend(project.getIsRecommend());
            
            Project updatedProject = projectRepository.save(existingProject);
            log.info("更新项目成功，项目ID：{}", projectId);
            return updatedProject;
            
        } catch (Exception e) {
            log.error("更新项目失败，项目ID：{}", projectId, e);
            throw new RuntimeException("更新项目失败", e);
        }
    }
    
    @Override
    @Transactional
    public void deleteProject(String projectId) {
        try {
            if (!projectRepository.existsById(projectId)) {
                throw new RuntimeException("项目不存在，项目ID：" + projectId);
            }
            
            projectRepository.deleteById(projectId);
            log.info("删除项目成功，项目ID：{}", projectId);
            
        } catch (Exception e) {
            log.error("删除项目失败，项目ID：{}", projectId, e);
            throw new RuntimeException("删除项目失败", e);
        }
    }
    
    @Override
    @Transactional
    public void deleteProjects(String[] projectIds) {
        try {
            for (String projectId : projectIds) {
                if (projectRepository.existsById(projectId)) {
                    projectRepository.deleteById(projectId);
                }
            }
            log.info("批量删除项目成功，删除数量：{}", projectIds.length);
            
        } catch (Exception e) {
            log.error("批量删除项目失败", e);
            throw new RuntimeException("批量删除项目失败", e);
        }
    }
    
    @Override
    @Transactional
    public Project setProjectHot(String projectId, Boolean isHot) {
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("项目不存在，项目ID：" + projectId));
            
            project.setIsHot(isHot);
            Project updatedProject = projectRepository.save(project);
            
            log.info("设置项目热门状态成功，项目ID：{}，热门状态：{}", projectId, isHot);
            return updatedProject;
            
        } catch (Exception e) {
            log.error("设置项目热门状态失败，项目ID：{}", projectId, e);
            throw new RuntimeException("设置项目热门状态失败", e);
        }
    }
    
    @Override
    @Transactional
    public Project setProjectRecommend(String projectId, Boolean isRecommend) {
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("项目不存在，项目ID：" + projectId));
            
            project.setIsRecommend(isRecommend);
            Project updatedProject = projectRepository.save(project);
            
            log.info("设置项目推荐状态成功，项目ID：{}，推荐状态：{}", projectId, isRecommend);
            return updatedProject;
            
        } catch (Exception e) {
            log.error("设置项目推荐状态失败，项目ID：{}", projectId, e);
            throw new RuntimeException("设置项目推荐状态失败", e);
        }
    }
    
    @Override
    @Transactional
    public Project updateProjectStatus(String projectId, String status) {
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("项目不存在，项目ID：" + projectId));
            
            project.setStatus(status);
            Project updatedProject = projectRepository.save(project);
            
            log.info("更新项目状态成功，项目ID：{}，新状态：{}", projectId, status);
            return updatedProject;
            
        } catch (Exception e) {
            log.error("更新项目状态失败，项目ID：{}", projectId, e);
            throw new RuntimeException("更新项目状态失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getProjectStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总项目数
            Long totalProjects = projectRepository.countAllProjects();
            statistics.put("totalProjects", totalProjects);
            
            // 热门项目数
            Long hotProjects = projectRepository.countHotProjects();
            statistics.put("hotProjects", hotProjects);
            
            // 推荐项目数
            Long recommendProjects = projectRepository.countRecommendProjects();
            statistics.put("recommendProjects", recommendProjects);
            
            // 按状态分组统计
            Object[][] statusStats = projectRepository.countProjectsByStatus();
            Map<String, Long> statusMap = new HashMap<>();
            for (Object[] stat : statusStats) {
                statusMap.put((String) stat[0], (Long) stat[1]);
            }
            statistics.put("statusStatistics", statusMap);
            
            // 按分类分组统计
            Object[][] categoryStats = projectRepository.countProjectsByCategory();
            Map<String, Long> categoryMap = new HashMap<>();
            for (Object[] stat : categoryStats) {
                categoryMap.put((String) stat[0], (Long) stat[1]);
            }
            statistics.put("categoryStatistics", categoryMap);
            
            return statistics;
            
        } catch (Exception e) {
            log.error("获取项目统计信息失败", e);
            throw new RuntimeException("获取项目统计信息失败", e);
        }
    }
    
    @Override
    public Page<Project> getProjectsByStatus(String status, Pageable pageable) {
        try {
            return projectRepository.findByStatus(status, pageable);
        } catch (Exception e) {
            log.error("按状态查询项目失败，状态：{}", status, e);
            throw new RuntimeException("查询项目失败", e);
        }
    }
    
    @Override
    public Page<Project> getProjectsByPriceRange(Double minPrice, Double maxPrice, Pageable pageable) {
        try {
            return projectRepository.findByPriceRange(minPrice, maxPrice, pageable);
        } catch (Exception e) {
            log.error("按价格范围查询项目失败，价格范围：{}-{}", minPrice, maxPrice, e);
            throw new RuntimeException("查询项目失败", e);
        }
    }
    
    @Override
    public Page<Project> getProjectSalesRanking(Pageable pageable) {
        try {
            return projectRepository.findAllByOrderBySalesCountDesc(pageable);
        } catch (Exception e) {
            log.error("获取项目销售排行失败", e);
            throw new RuntimeException("获取项目销售排行失败", e);
        }
    }
    
    @Override
    public Page<Project> getProjectRatingRanking(Pageable pageable) {
        try {
            return projectRepository.findAllByOrderByRatingDesc(pageable);
        } catch (Exception e) {
            log.error("获取项目评分排行失败", e);
            throw new RuntimeException("获取项目评分排行失败", e);
        }
    }
    
    @Override
    public Page<Project> getActiveProjectSalesRanking(Pageable pageable) {
        try {
            return projectRepository.findByStatusOrderBySalesCountDesc("active", pageable);
        } catch (Exception e) {
            log.error("获取启用状态项目销售排行失败", e);
            throw new RuntimeException("获取项目销售排行失败", e);
        }
    }
    
    @Override
    public Page<Project> getActiveProjectRatingRanking(Pageable pageable) {
        try {
            return projectRepository.findByStatusOrderByRatingDesc("active", pageable);
        } catch (Exception e) {
            log.error("获取启用状态项目评分排行失败", e);
            throw new RuntimeException("获取项目评分排行失败", e);
        }
    }
}