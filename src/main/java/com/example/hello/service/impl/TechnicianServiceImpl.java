package com.example.hello.service.impl;

import com.example.hello.entity.Technician;
import com.example.hello.repository.TechnicianRepository;
import com.example.hello.service.TechnicianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TechnicianServiceImpl implements TechnicianService {
    
    @Autowired
    private TechnicianRepository technicianRepository;
    
    @Override
    public Page<Technician> getTechnicianList(Pageable pageable) {
        // 实际应该从数据库查询技师列表
        // 这里返回一个空的Page对象作为演示
        return Page.empty(pageable);
    }
    
    @Override
    public Page<Technician> searchTechnicians(String keyword, Pageable pageable) {
        // 实际应该从数据库搜索技师
        // 这里返回一个空的Page对象作为演示
        return Page.empty(pageable);
    }
    
    @Override
    public Page<Technician> getTechniciansByStore(UUID storeId, Pageable pageable) {
        // 实际应该从数据库查询指定门店的技师列表
        // 这里返回一个空的Page对象作为演示
        return Page.empty(pageable);
    }
    
    @Override
    public Technician getTechnicianDetail(UUID technicianId) {
        // 实际应该从数据库查询技师详情
        // 为了演示，我们创建一个模拟的技师
        Technician technician = new Technician();
        technician.setId(technicianId.toString());
        technician.setName("李技师");
        technician.setAvatar("https://example.com/technician.jpg");
        technician.setRating(4.9);
        technician.setExperience("5");
        
        // 添加服务项目
        List<String> services = new ArrayList<>();
        services.add("精油按摩");
        services.add("面部护理");
        technician.setServices(services);
        
        return technician;
    }
}