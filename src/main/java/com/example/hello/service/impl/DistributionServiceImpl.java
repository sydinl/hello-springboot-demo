package com.example.hello.service.impl;

import com.example.hello.entity.DistributionData;
import com.example.hello.entity.DistributionOrder;
import com.example.hello.entity.User;
import com.example.hello.repository.DistributionOrderRepository;
import com.example.hello.repository.UserRepository;
import com.example.hello.service.DistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DistributionServiceImpl implements DistributionService {

    @Autowired
    private DistributionOrderRepository distributionOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public DistributionData getDistributionData(UUID userId) {
        // 模拟获取分销中心数据
        DistributionData data = new DistributionData();
        data.setTotalCommission(5000.0);
        data.setAvailableCommission(3000.0);
        data.setTeamCount(50);
        data.setTodayOrderCount(10);
        return data;
    }

    @Override
    public Page<DistributionOrder> getDistributionOrders(UUID userId, Pageable pageable) {
        // 模拟获取分销订单列表
        return Page.empty(pageable);
    }

    @Override
    public Page<DistributionOrder> getDistributionOrdersByStatus(UUID userId, String status, Pageable pageable) {
        // 模拟获取指定状态的分销订单
        return Page.empty(pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindReferrer(String userId, String referrerId) {
        if (userId == null || referrerId == null || userId.trim().isEmpty() || referrerId.trim().isEmpty()) {
            return false;
        }
        if (userId.equals(referrerId)) {
            return false; // 不能绑定自己
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        if (user.getReferrerId() != null && !user.getReferrerId().isEmpty()) {
            return false; // 已绑定过推荐人，不可变更
        }
        User referrer = userRepository.findById(referrerId).orElse(null);
        if (referrer == null) {
            return false; // 推荐人不存在
        }
        // 防止循环：推荐人的上级链中不能包含当前用户（不能绑定自己的下级）
        String up = referrer.getReferrerId();
        while (up != null && !up.isEmpty()) {
            if (userId.equals(up)) {
                return false;
            }
            User upUser = userRepository.findById(up).orElse(null);
            up = upUser != null ? upUser.getReferrerId() : null;
        }
        user.setReferrerId(referrerId);
        userRepository.save(user);
        return true;
    }
}