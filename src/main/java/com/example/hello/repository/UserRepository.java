package com.example.hello.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.hello.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    boolean existsByPhone(String phone);
    
    // 微信相关查询方法
    Optional<User> findByOpenId(String openId);
    
    Optional<User> findByUnionId(String unionId);
    
    boolean existsByOpenId(String openId);
    
    boolean existsByUnionId(String unionId);

    /** 分销：按推荐人ID查询直接下级（一级） */
    List<User> findByReferrerId(String referrerId);

    /** 分销：统计某推荐人的直接下级人数 */
    long countByReferrerId(String referrerId);
}