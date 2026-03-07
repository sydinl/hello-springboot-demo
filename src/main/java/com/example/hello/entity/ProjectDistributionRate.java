package com.example.hello.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 按项目（商品）设置的一级/二级分销比例，覆盖全局默认。
 */
@Data
@Entity
@Table(name = "project_distribution_rates")
public class ProjectDistributionRate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** 项目ID，对应 projects.id / order_items.project_id */
    @Column(name = "project_id", nullable = false, unique = true, length = 36)
    private String projectId;

    /** 一级推荐人佣金比例，如 0.10 表示 10% */
    @Column(name = "level1_rate", nullable = false)
    private Double level1Rate;

    /** 二级推荐人佣金比例，如 0.05 表示 5% */
    @Column(name = "level2_rate", nullable = false)
    private Double level2Rate;
}
