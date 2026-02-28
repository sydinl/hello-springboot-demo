package com.example.hello.repository;

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
}