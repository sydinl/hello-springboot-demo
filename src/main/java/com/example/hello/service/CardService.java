package com.example.hello.service;

import com.example.hello.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface CardService {
    // 获取用户卡券列表
    Page<Card> getUserCards(UUID userId, Pageable pageable);
    
    // 获取用户指定类型的卡券
    Page<Card> getUserCardsByType(UUID userId, String type, Pageable pageable);
    
    // 激活卡券
    Card activateCard(UUID userId, String cardCode);
}