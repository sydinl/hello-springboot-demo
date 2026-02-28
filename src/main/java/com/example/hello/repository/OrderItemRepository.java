package com.example.hello.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.hello.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    
    // 根据订单ID查询订单项
    List<OrderItem> findByOrderId(String orderId);
    
    // 根据订单ID删除订单项
    void deleteByOrderId(String orderId);
}
