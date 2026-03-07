package com.example.hello.service.impl;

import com.example.hello.entity.DistributionConfig;
import com.example.hello.entity.DistributionData;
import com.example.hello.entity.DistributionOrder;
import com.example.hello.entity.Order;
import com.example.hello.entity.OrderItem;
import com.example.hello.entity.ProjectDistributionRate;
import com.example.hello.entity.User;
import com.example.hello.repository.DistributionConfigRepository;
import com.example.hello.repository.DistributionOrderRepository;
import com.example.hello.repository.OrderItemRepository;
import com.example.hello.repository.OrderRepository;
import com.example.hello.repository.ProjectDistributionRateRepository;
import com.example.hello.repository.UserRepository;
import com.example.hello.repository.WithdrawalRepository;
import com.example.hello.service.DistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DistributionServiceImpl implements DistributionService {

    @Autowired
    private DistributionOrderRepository distributionOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private DistributionConfigRepository distributionConfigRepository;

    @Autowired
    private ProjectDistributionRateRepository projectDistributionRateRepository;

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @Override
    public DistributionData getDistributionData(UUID userId) {
        String uid = userId != null ? userId.toString() : null;
        if (uid == null || uid.isBlank()) {
            return emptyDistributionData();
        }
        DistributionData data = new DistributionData();
        Double total = distributionOrderRepository.sumCommissionByReferrerId(uid);
        data.setTotalCommission(total != null ? total : 0.0);
        Double pending = distributionOrderRepository.sumCommissionByReferrerIdAndStatus(uid, "pending");
        Double pendingWithdraw = withdrawalRepository.sumAmountByUserIdAndStatus(uid, "pending");
        Double completedWithdraw = withdrawalRepository.sumAmountByUserIdAndStatus(uid, "completed");
        double pendingVal = pending != null ? pending : 0;
        double pendingWithdrawVal = pendingWithdraw != null ? pendingWithdraw : 0;
        data.setAvailableCommission(Math.max(0, pendingVal - pendingWithdrawVal));
        data.setWithdrawnCommission(completedWithdraw != null ? completedWithdraw : 0.0);
        long level1 = userRepository.countByReferrerId(uid);
        List<User> level1Users = userRepository.findByReferrerId(uid);
        long level2 = 0;
        for (User u : level1Users) {
            if (u.getId() != null) {
                level2 += userRepository.countByReferrerId(u.getId());
            }
        }
        data.setTeamCount((int) (level1 + level2));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long todayCount = distributionOrderRepository.countByReferrerIdAndCreateTimeAfter(uid, cal.getTime());
        data.setTodayOrderCount((int) todayCount);
        long totalOrders = distributionOrderRepository.countByReferrerId(uid);
        data.setTotalOrderCount((int) totalOrders);
        return data;
    }

    private static DistributionData emptyDistributionData() {
        DistributionData data = new DistributionData();
        data.setTotalCommission(0.0);
        data.setAvailableCommission(0.0);
        data.setWithdrawnCommission(0.0);
        data.setTeamCount(0);
        data.setTodayOrderCount(0);
        data.setTotalOrderCount(0);
        return data;
    }

    @Override
    public Page<DistributionOrder> getDistributionOrders(UUID userId, Pageable pageable) {
        if (userId == null) {
            return Page.empty(pageable);
        }
        String uid = userId.toString();
        return distributionOrderRepository.findByReferrerId(uid, pageable);
    }

    @Override
    public Page<DistributionOrder> getDistributionOrdersByStatus(UUID userId, String status, Pageable pageable) {
        if (userId == null || status == null || status.isBlank()) {
            return getDistributionOrders(userId, pageable);
        }
        String uid = userId.toString();
        return distributionOrderRepository.findByReferrerIdAndStatus(uid, status, pageable);
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
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        if (items == null || items.isEmpty()) {
            return;
        }
        double orderTotal = order.getTotalPrice() != null && order.getTotalPrice() > 0
                ? order.getTotalPrice() : 0;
        double orderFinal = order.getFinalAmount() != null && order.getFinalAmount() > 0
                ? order.getFinalAmount() : orderTotal;
        if (orderTotal <= 0) {
            return;
        }
        // 按商品粒度：每个订单项按比例分摊实付金额，再按该项目的一级/二级比例累加佣金
        double commission1 = 0;
        double commission2 = 0;
        for (OrderItem item : items) {
            BigDecimal p = item.getPrice();
            int q = item.getQuantity() != null ? item.getQuantity() : 1;
            double itemTotal = (p != null ? p.doubleValue() : 0) * q;
            double itemShare = (itemTotal / orderTotal) * orderFinal;
            String projectId = item.getProjectId();
            double l1 = getLevel1RateForProject(projectId);
            double l2 = getLevel2RateForProject(projectId);
            commission1 += itemShare * l1;
            commission2 += itemShare * l2;
        }
        commission1 = Math.round(commission1 * 100) / 100.0;
        commission2 = Math.round(commission2 * 100) / 100.0;
        String customerName = buyer.getFullName() != null ? buyer.getFullName() : buyer.getUsername();
        if (customerName == null) {
            customerName = buyer.getPhone() != null ? buyer.getPhone() : buyerUserId;
        }
        String level1ReferrerId = buyer.getReferrerId();
        DistributionOrder d1 = new DistributionOrder();
        d1.setOrderId(orderId);
        d1.setReferrerId(level1ReferrerId);
        d1.setReferrerLevel(1);
        d1.setCustomerName(customerName);
        d1.setCommission(commission1);
        d1.setStatus("pending");
        distributionOrderRepository.save(d1);
        User level1User = userRepository.findById(level1ReferrerId).orElse(null);
        if (level1User != null && level1User.getReferrerId() != null && !level1User.getReferrerId().isBlank()) {
            String level2ReferrerId = level1User.getReferrerId();
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

    private double getLevel1RateForProject(String projectId) {
        if (projectId != null && !projectId.isBlank()) {
            Optional<ProjectDistributionRate> opt = projectDistributionRateRepository.findByProjectId(projectId);
            if (opt.isPresent()) {
                return opt.get().getLevel1Rate() != null ? opt.get().getLevel1Rate() : 0.10;
            }
        }
        DistributionConfig cfg = getOrCreateGlobalConfig();
        return cfg.getLevel1Rate() != null ? cfg.getLevel1Rate() : 0.10;
    }

    private double getLevel2RateForProject(String projectId) {
        if (projectId != null && !projectId.isBlank()) {
            Optional<ProjectDistributionRate> opt = projectDistributionRateRepository.findByProjectId(projectId);
            if (opt.isPresent()) {
                return opt.get().getLevel2Rate() != null ? opt.get().getLevel2Rate() : 0.05;
            }
        }
        DistributionConfig cfg = getOrCreateGlobalConfig();
        return cfg.getLevel2Rate() != null ? cfg.getLevel2Rate() : 0.05;
    }

    private DistributionConfig getOrCreateGlobalConfig() {
        return distributionConfigRepository.findById("default")
                .orElseGet(() -> {
                    DistributionConfig c = new DistributionConfig();
                    c.setId("default");
                    c.setLevel1Rate(0.10);
                    c.setLevel2Rate(0.05);
                    return distributionConfigRepository.save(c);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public DistributionConfig getGlobalConfig() {
        return getOrCreateGlobalConfig();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DistributionConfig updateGlobalConfig(double level1Rate, double level2Rate) {
        DistributionConfig cfg = getOrCreateGlobalConfig();
        cfg.setLevel1Rate(level1Rate);
        cfg.setLevel2Rate(level2Rate);
        return distributionConfigRepository.save(cfg);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDistributionRate> listProjectRates() {
        return projectDistributionRateRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDistributionRate getProjectRate(String projectId) {
        return projectDistributionRateRepository.findByProjectId(projectId).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectDistributionRate saveProjectRate(String projectId, double level1Rate, double level2Rate) {
        if (projectId == null || projectId.isBlank()) {
            throw new IllegalArgumentException("projectId 不能为空");
        }
        ProjectDistributionRate r = projectDistributionRateRepository.findByProjectId(projectId)
                .orElse(new ProjectDistributionRate());
        r.setProjectId(projectId);
        r.setLevel1Rate(level1Rate);
        r.setLevel2Rate(level2Rate);
        return projectDistributionRateRepository.save(r);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectRate(String projectId) {
        if (projectId != null && !projectId.isBlank()) {
            projectDistributionRateRepository.deleteByProjectId(projectId);
        }
    }
}