package com.example.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.hello.entity.Project;
import com.example.hello.entity.User;
import com.example.hello.repository.ProjectRepository;
import com.example.hello.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataInitializationService implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initializeAdminUser();
        initializeTestProjects();
    }
    
    private void initializeAdminUser() {
        // 检查是否已存在管理员用户
        if (userRepository.existsByUsername("admin")) {
            log.info("管理员用户已存在，跳过创建");
            return;
        }
        
        // 创建默认管理员用户
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("Wdjemvsts@118"));
        adminUser.setPhone("13800138000");
        adminUser.setFullName("系统管理员");
        adminUser.setRole("ADMIN");
        adminUser.setEnabled(true);
        // adminUser.setId(UUID.randomUUID().toString());
        // Let Hibernate generate the UUID automatically
        userRepository.save(adminUser);
        log.info("创建默认管理员用户成功 - 用户名: admin, 密码: admin123");
    }
    
    private void initializeTestProjects() {
        // 检查是否已存在项目数据
        if (projectRepository.count() > 0) {
            log.info("项目数据已存在，跳过创建");
            return;
        }
        
        // 创建测试项目数据
        createTestProject("经典SPA护理", "SPA护理", 299.0, "90分钟", "active", true, false);
        createTestProject("深层清洁护理", "美容护理", 199.0, "60分钟", "active", false, true);
        createTestProject("抗衰老护理", "美容护理", 399.0, "120分钟", "active", true, true);
        createTestProject("舒缓按摩", "SPA护理", 159.0, "45分钟", "active", false, false);
        createTestProject("美白护理", "美容护理", 259.0, "75分钟", "draft", false, false);
        
        log.info("创建测试项目数据成功");
    }
    
    private void createTestProject(String name, String category, Double price, String duration, 
                                 String status, Boolean isHot, Boolean isRecommend) {
        Project project = new Project();
        project.setName(name);
        project.setDescription("这是一个" + name + "服务，提供专业的护理体验");
        project.setPrice(price);
        project.setDuration(duration);
        project.setCategory(category);
        project.setStatus(status);
        project.setIsHot(isHot);
        project.setIsRecommend(isRecommend);
        project.setSalesCount(0);
        project.setRating(0.0);
        project.setDetails("详细的服务介绍和注意事项");
        project.setImage("https://via.placeholder.com/300x200?text=" + name);
        
        projectRepository.save(project);
        log.info("创建测试项目: {}", name);
    }
}
