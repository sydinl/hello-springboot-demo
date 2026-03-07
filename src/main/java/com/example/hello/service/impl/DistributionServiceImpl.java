package com.example.hello.service.impl;

import com.example.hello.entity.DistributionData;
import com.example.hello.entity.DistributionOrder;
import com.example.hello.entity.Order;
import com.example.hello.entity.User;
import com.example.hello.repository.DistributionOrderRepository;
import com.example.hello.repository.OrderRepository;
import com.example.hello.repository.UserRepository;
import com.example.hello.service.DistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private OrderRepository orderRepository;

    @Value("${distribution.level1-rate:0.10}")
    private double level1Rate;
    @Value("${distribution.level2-rate:0.05}")
    private double level2Rate;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDistributionOrdersForPaidOrder(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            return;
        }
        if (distributionOrderRepository.existsByOrderId(orderId)) {
            return; // 已生成过分销订单，防重复
        }
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !"paid".equals(order.getStatus())) {
            return;
        }
        String buyerUserId = order.getUserId();
        if (buyerUserId == null || buyerUserId.isBlank()) {
            return;
        }
        User buyer = userRepository.findById(buyerUserId).orElse(null);
        if (buyer == null || buyer.getReferrerId() == null || buyer.getReferrerId().isBlank()) {
            return; // 无一级推荐人，不生成分销订单
        }
        double baseAmount = order.getFinalAmount() != null && order.getFinalAmount() > 0
                ? order.getFinalAmount() : (order.getTotalPrice() != null ? order.getTotalPrice() : 0);
        if (baseAmount <= 0) {
            return;
        }
        String customerName = buyer.getFullName() != null ? buyer.getFullName() : buyer.getUsername();
        if (customerName == null) {
            customerName = buyer.getPhone() != null ? buyer.getPhone() : buyerUserId;
        }
        // 一级推荐人
        String level1ReferrerId = buyer.getReferrerId();
        double commission1 = Math.round(baseAmount * level1Rate * 100) / 100.0;
        DistributionOrder d1 = new DistributionOrder();
        d1.setOrderId(orderId);
        d1.setReferrerId(level1ReferrerId);
        d1.setReferrerLevel(1);
        d1.setCustomerName(customerName);
        d1.setCommission(commission1);
        d1.setStatus("pending");
        distributionOrderRepository.save(d1);
        // 二级推荐人
        User level1User = userRepository.findById(level1ReferrerId).orElse(null);
        if (level1User != null && level1User.getReferrerId() != null && !level1User.getReferrerId().isBlank()) {
            String level2ReferrerId = level1User.getReferrerId();
            double commission2 = Math.round(baseAmount * level2Rate * 100) / 100.0;
            DistributionOrder d2 = new DistributionOrder();
            d2.setOrderId(orderId);
            d2.setReferrerId(level2ReferrerId);
            d2.setReferrerLevel(2);
            d2.setCustomerName(customerName);
            d2.setCommission(commission2);
            d2.setStatus("pending");
            distributionOrderRepository.save(d2);
        }
    }
}