package com.example.hello.entity;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.Column;
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
@Table(name = "users")
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", length = 36)
    private String id;
    
    @Column(name = "nickname")
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "phone", unique = true, nullable = true)
    private String phone;
    
    @Column(name = "real_name")
    private String fullName;
    
    @Column(name = "avatar")
    private String avatar;
    
    @Column(name = "gender")
    private String gender;
    
    @Column(name = "birthdate")
    private Date birthdate;
    
    @Column(name = "points")
    private Integer points = 0;
    
    @Column(name = "balance")
    private Double balance = 0.0;
    
    @Column(name = "member_level")
    private String memberLevel = "普通会员";
    
    @Column(name = "create_time")
    private Date createTime;
    
    @Column(name = "update_time")
    private Date updateTime;
    
    // Security-related fields (not in database schema)
    private Boolean enabled = true;
    private Boolean accountNonExpired = true;
    private Boolean accountNonLocked = true;
    private Boolean credentialsNonExpired = true;
    
    // Simple role field instead of many-to-many relationship
    @Column(name = "role")
    private String role = "USER";
    
    // 微信相关字段
    @Column(name = "open_id", unique = true)
    private String openId;
    
    @Column(name = "union_id")
    private String unionId;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "province")
    private String province;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "language")
    private String language;
    
    @Column(name = "last_login_time")
    private Date lastLoginTime;

    /** 分销：推荐人用户ID（一级），为空表示未通过推荐注册 */
    @Column(name = "referrer_id")
    private String referrerId;
    
    @PrePersist
    protected void onCreate() {
        // ID已经在构造函数中初始化，这里只需要设置时间
        if (createTime == null) {
            createTime = new Date();
        }
        updateTime = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
    
    // UserDetails interface methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}