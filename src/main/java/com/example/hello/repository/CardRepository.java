package com.example.hello.repository;

import com.example.hello.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
    // 按用户ID分页查询卡券
    Page<Card> findByUserId(UUID userId, Pageable pageable);
    
    // 按用户ID和类型分页查询卡券
    Page<Card> findByUserIdAndType(UUID userId, String type, Pageable pageable);
}