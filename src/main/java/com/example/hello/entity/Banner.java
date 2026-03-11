package com.example.hello.entity;

import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "banners")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;
    private String imageUrl;

    /**
     * 跳转类型：0=无跳转，1=小程序内部页面，2=外部H5链接
     */
    private Integer jumpType;

    /**
     * 跳转目标（小程序页面路径或URL）
     */
    private String jumpTarget;

    /**
     * 展示位置，例如：home
     */
    private String position;

    /**
     * 排序值，越大越靠前
     */
    private Integer sort;

    /**
     * 是否启用
     */
    private Boolean enabled;

    private Date startTime;
    private Date endTime;

    private Date createTime;
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        createTime = now;
        updateTime = now;
        if (enabled == null) {
            enabled = Boolean.TRUE;
        }
        if (position == null) {
            position = "home";
        }
        if (jumpType == null) {
            jumpType = 0;
        }
        if (sort == null) {
            sort = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
}

