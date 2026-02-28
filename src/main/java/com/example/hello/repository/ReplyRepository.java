package com.example.hello.repository;

import com.example.hello.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, String> {
    // 根据评价ID查询回复
    Reply findByReviewId(String reviewId);
}