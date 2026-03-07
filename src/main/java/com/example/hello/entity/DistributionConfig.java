package com.example.hello.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 分销全局配置（单行）：一级/二级默认佣金比例，管理员可修改。
 */
@Data
@Entity
@Table(name = "distribution_config")
public class DistributionConfig {

    /** 固定主键，全局仅此一行 */
    @Id
    @Column(name = "id", length = 32)
    private String id = "default";

    /** 一级推荐人默认佣金比例，如 0.10 表示 10% */
    @Column(name = "level1_rate", nullable = false)
    private Double level1Rate = 0.10;

    /** 二级推荐人默认佣金比例，如 0.05 表示 5% */
    @Column(name = "level2_rate", nullable = false)
    private Double level2Rate = 0.05;
}
