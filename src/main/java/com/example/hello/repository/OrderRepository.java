package com.example.hello.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.hello.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.orderId = :id")
    Optional<Order> findByIdWithItems(@Param("id") String id);

    Page<Order> findByUserId(String userId, Pageable pageable);
    
    Page<Order> findByUserIdAndStatus(String userId, String status, Pageable pageable);
    
    // 管理员查询方法
    Page<Order> findByStatus(String status, Pageable pageable);
    
    Page<Order> findByOrderIdContaining(String orderId, Pageable pageable);
    
    // 统计方法
    long countByStatus(String status);
    
    // 统计用户指定状态的订单数量
    long countByUserIdAndStatus(String userId, String status);
    
    // 根据订单号查询
    Optional<Order> findByOrderNo(String orderNo);

    // 根据核销码查询订单
    Optional<Order> findByVerificationCode(String verificationCode);
}