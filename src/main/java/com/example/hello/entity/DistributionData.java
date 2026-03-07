package com.example.hello.entity;

import lombok.Data;

@Data
public class DistributionData {
    
    /** 累计获得佣金总额 */
    private Double totalCommission;
    /** 可提现佣金（未结算/待结算） */
    private Double availableCommission;
    /** 已提现佣金（提现功能实现后从提现记录汇总） */
    private Double withdrawnCommission;
    /** 团队人数（一级+二级） */
    private Integer teamCount;
    /** 今日产生佣金订单数 */
    private Integer todayOrderCount;
    /** 分销订单总数（用于展示「分销订单」入口数字） */
    private Integer totalOrderCount;
}