package com.example.hello.service;

import com.example.hello.entity.DistributionConfig;
import com.example.hello.entity.DistributionData;
import com.example.hello.entity.DistributionOrder;
import com.example.hello.entity.ProjectDistributionRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface DistributionService {
    // 获取分销中心数据
    DistributionData getDistributionData(UUID userId);
    
    // 获取分销订单列表
    Page<DistributionOrder> getDistributionOrders(UUID userId, Pageable pageable);
    
    // 获取指定状态的分销订单
    Page<DistributionOrder> getDistributionOrdersByStatus(UUID userId, String status, Pageable pageable);

    /**
     * 绑定推荐人（一级）。仅当前用户未绑定过时有效，且不能绑定自己。
     * @return 成功为 true，已绑定过或参数无效为 false
     */
    boolean bindReferrer(String userId, String referrerId);

    /**
     * 订单支付成功后生成二级分销订单（一级、二级推荐人各一条，若有）。
     * 按订单内每个商品（项目）配置的比例汇总佣金。
     */
    void createDistributionOrdersForPaidOrder(String orderId);

    // ---------- 管理员：全局与按项目比例 ----------
    DistributionConfig getGlobalConfig();
    DistributionConfig updateGlobalConfig(double level1Rate, double level2Rate);
    List<ProjectDistributionRate> listProjectRates();
    ProjectDistributionRate getProjectRate(String projectId);
    ProjectDistributionRate saveProjectRate(String projectId, double level1Rate, double level2Rate);
    void deleteProjectRate(String projectId);
}