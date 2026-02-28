package com.example.hello.controller;

import com.example.hello.entity.Card;
import com.example.hello.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/card")
public class CardController {
    
    @Autowired
    private CardService cardService;
    
    // 获取用户卡券列表
    @GetMapping("/list")
    public ResponseEntity<Page<Card>> getUserCards(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Card> cards = cardService.getUserCards(userId, pageable);
        return ResponseEntity.ok(cards);
    }
    
    // 获取用户指定类型的卡券
    @GetMapping("/listByType")
    public ResponseEntity<Page<Card>> getUserCardsByType(
            @RequestParam UUID userId,
            @RequestParam String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Card> cards = cardService.getUserCardsByType(userId, type, pageable);
        return ResponseEntity.ok(cards);
    }
    
    // 激活卡券
    @PostMapping("/activate")
    public ResponseEntity<Card> activateCard(@RequestParam UUID userId, @RequestParam String cardCode) {
        Card card = cardService.activateCard(userId, cardCode);
        return ResponseEntity.ok(card);
    }
}