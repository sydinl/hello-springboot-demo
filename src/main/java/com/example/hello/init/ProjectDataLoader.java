package com.example.hello.init;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.hello.entity.Project;
import com.example.hello.entity.ProjectImage;
import com.example.hello.repository.ProjectImageRepository;
import com.example.hello.repository.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ProjectDataLoader implements CommandLineRunner {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectImageRepository projectImageRepository;

    @Override
    public void run(String... args) throws Exception {
        // 检查数据库中是否已有数据，避免重复插入
        if (projectRepository.count() == 0) {
            // 读取projects.json文件
            ObjectMapper mapper = new ObjectMapper();
            File projectsFile = new File("projects.json");
            InputStream inputStream = new FileInputStream(projectsFile);
            
            // 解析JSON数据
            List<Map<String, Object>> projectDataList = mapper.readValue(inputStream, List.class);
            
            List<Project> projects = new ArrayList<>();
            List<ProjectImage> projectImages = new ArrayList<>();
            
            // 遍历数据并创建实体对象
            for (Map<String, Object> projectData : projectDataList) {
                Project project = new Project();
                
                // 设置基本信息
                // 注意：这里使用UUID生成新的ID，而不是使用JSON中的id
                UUID projectUuid = UUID.randomUUID();
                
                // 设置项目名称
                if (projectData.containsKey("name")) {
                    project.setName((String) projectData.get("name"));
                } else {
                    project.setName("未命名项目");
                }
                
                // 设置项目描述
                if (projectData.containsKey("desc")) {
                    project.setDescription((String) projectData.get("desc"));
                }
                
                // 设置价格
                if (projectData.containsKey("price")) {
                    Object priceObj = projectData.get("price");
                    if (priceObj instanceof Number) {
                        project.setPrice(((Number) priceObj).doubleValue());
                    }
                }
                
                // 设置分类
                if (projectData.containsKey("category")) {
                    project.setCategory((String) projectData.get("category"));
                }
                
                // 设置主图
                if (projectData.containsKey("img")) {
                    project.setImage((String) projectData.get("img"));
                }
                
                // 设置其他默认值
                project.setSalesCount(0);
                project.setRating(5.0);
                project.setIsHot(false);
                
                // 保存项目
                projects.add(project);
                
                // 创建项目图片关联
                if (projectData.containsKey("img")) {
                    ProjectImage image = new ProjectImage();
                    image.setProjectId(projectUuid);
                    image.setUrl((String) projectData.get("img"));
                    projectImages.add(image);
                }
            }
            
            // 批量保存到数据库
            projectRepository.saveAll(projects);
            projectImageRepository.saveAll(projectImages);
            
            System.out.println("成功从projects.json导入" + projects.size() + "个项目数据到数据库");
        } else {
            System.out.println("数据库中已有项目数据，跳过导入");
        }
    }
}