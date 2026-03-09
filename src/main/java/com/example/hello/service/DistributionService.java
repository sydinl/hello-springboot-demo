package com.example.hello.service;

import com.example.hello.dto.BindReferrerResult;
import com.example.hello.entity.DistributionConfig;
import com.example.hello.entity.DistributionData;
import com.example.hello.entity.DistributionOrder;
import com.example.hello.entity.ProjectDistributionRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
     * 绑定推荐人（一级）。要求当前用户与推荐人均已绑手机；仅未绑定过时有效。
     * @return 结果枚举，便于前端区分「需绑手机」等
     */
    BindReferrerResult bindReferrer(String userId, String referrerId);

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

    /** 分销排行：按累计佣金倒序，返回前 limit 条（含用户信息） */
    List<Map<String, Object>> getCommissionRanking(int limit);
}